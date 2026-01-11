from collections import defaultdict
from itertools import zip_longest
import json
from datetime import datetime
from django.shortcuts import render, get_object_or_404, redirect
from django.contrib.auth.decorators import login_required
from django.utils import timezone
from django.contrib.auth import *

from main.models import AuthUser
from questionarios.filters import QuestionarioFilter
from .forms import QuestionarioForm
from .models import RespostaUsuario, TemaPergunta, EstadoQuestionario, OpcaoEscolhaMultipla, Questionario, Pergunta, Resposta, TipoPergunta, UtilizadorQuestionario  
from .tables import PublicadoTable, TemaTable, EstadoTable, QuestionarioTable
from anoletivo.models import ano_letivo

import csv
from django.http import HttpResponse
from reportlab.pdfgen import canvas
from django.db.models import Q
from django.db.models import Subquery


def listar_questionarios(request):
    queryset = Questionario.objects.all()  # Exclude archived questionnaires by default

    estado_ids = request.GET.getlist('estado')
    if estado_ids:
        estado_ids = [int(eid) for eid in estado_ids]  # Ensure all are integers
        queryset = queryset.filter(estado__id__in=estado_ids)
        if 7 not in estado_ids:
            queryset = queryset.exclude(estado__id=7)
    else:
        queryset = queryset.exclude(estado__id=7)


    anoletivo_ids = request.GET.getlist('anoletivoid')
    if anoletivo_ids:
        anoletivo_ids = [int(al_id) for al_id in anoletivo_ids]
        questionario_ids = ano_letivo.objects.filter(id__in=anoletivo_ids).values_list('questionario_id', flat=True)
        queryset = queryset.filter(id__in=questionario_ids)


    questionarios_filter = QuestionarioFilter(request.GET, queryset=queryset)
    table = QuestionarioTable(questionarios_filter.qs, request_user=request.user)

    return render(request, 'questionarios/listar_questionarios.html', {
        'table': table,
        'filter': questionarios_filter,
    })


def criar_questionario(request):
    anos_letivos = ano_letivo.objects.all()
    if request.method == 'POST':
        form = QuestionarioForm(request.POST)
        print(request.POST)
        if form.is_valid():
            questionario = form.save(commit=False)
            questionario.data_criacao = timezone.now()
            questionario.data_submissao = request.POST.get('dia_inicio')

            anoletivo_id = request.POST.get('ano_letivo')
            if anoletivo_id:
                ano_letivo_instance = ano_letivo.objects.get(id=anoletivo_id)
                questionario.anoletivoid = ano_letivo.objects.get(id=anoletivo_id)

            action = request.POST.get('action')
            if action == 'save_production':
                estado_inicial = EstadoQuestionario.objects.get(estado='Em Produção')
            elif action == 'save_created':
                estado_inicial = EstadoQuestionario.objects.get(estado='Criado')
            questionario.estado = estado_inicial
            questionario.save()

            perguntas = request.POST.getlist('perguntas[]')
            tipos_pergunta = request.POST.getlist('tipos_pergunta[]')
            temas_pergunta = request.POST.getlist('temas[]')
            for i, conteudo in enumerate(perguntas):
                tipo = tipos_pergunta[i]
                tipo_pergunta = TipoPergunta.objects.get(descricao=tipo)
                tema_id = temas_pergunta[i]
                tema_pergunta = TemaPergunta.objects.get(id=tema_id) 
                pergunta = Pergunta.objects.create(
                    conteudo=conteudo,
                    questionario=questionario,
                    tipo_pergunta=tipo_pergunta,
                    tema=tema_pergunta,
                    ano_letivo=ano_letivo_instance 
                )

                if tipo == 'escolha múltipla':
                    opcoes = request.POST.getlist(f'opcoes_{i}[]')
                    for texto_opcao in opcoes:
                        OpcaoEscolhaMultipla.objects.create(
                            pergunta=pergunta,
                            texto_opcao=texto_opcao
                        )

                elif tipo == 'escalar':
                    pergunta.valor_minimo = request.POST.get(f'valor_minimo[{i}]', None)
                    pergunta.significado_minimo = request.POST.get(f'significado_minimo[{i}]', '')
                    pergunta.valor_maximo = request.POST.get(f'valor_maximo[{i}]', None)
                    pergunta.significado_maximo = request.POST.get(f'significado_maximo[{i}]', '')
                    pergunta.save()

            UtilizadorQuestionario.objects.create(utilizadoruser_ptr_id=request.user.id, questionario=questionario)
            
            if action == 'save_production':
                return redirect('main:mensagem',26)
            elif action == 'save_created':      
                return redirect('main:mensagem',27)
    else:
        form = QuestionarioForm()

    temas = TemaPergunta.objects.all()    

    return render(request, 'questionarios/criar_questionario.html', {
        'form': form, 
        'anos_letivos': anos_letivos,
        'temas': temas
    })



def alterar_questionario(request, questionario_id):
    questionario = get_object_or_404(Questionario, id=questionario_id)
    anos_letivos = ano_letivo.objects.all()
    temas = TemaPergunta.objects.all()
    temas_json = json.dumps(list(temas.values('id', 'tema')), ensure_ascii=False)
    tipos_pergunta = TipoPergunta.objects.all()
    tipos_map = {tipo.descricao: tipo.id for tipo in tipos_pergunta}
    tipos_map_json = json.dumps(tipos_map)

    if request.method == 'POST':
        form = QuestionarioForm(request.POST, instance=questionario)
        if form.is_valid():
            saved_questionario = form.save(commit=False)
            anoletivo_id = form.cleaned_data.get('ano_letivo')
            if anoletivo_id:
                saved_questionario.ano_letivo = anoletivo_id
            data_submissao = request.POST.get('data_submissao')
            if data_submissao:
                saved_questionario.data_submissao = datetime.strptime(data_submissao, '%Y-%m-%d').date()

            saved_questionario.save()

            action = request.POST.get('action')
            if action == 'save_production':
                estado_inicial = EstadoQuestionario.objects.get(estado='Em Produção')
            elif action == 'save_created':
                estado_inicial = EstadoQuestionario.objects.get(estado='Criado')
            saved_questionario.estado = estado_inicial
            saved_questionario.save()

            print("Action:", action)
            print("Questionário Estado:", saved_questionario.estado)

            perguntas_ids_enviados = request.POST.getlist('perguntas_ids[]')
            print("IDs de Perguntas Enviadas:", perguntas_ids_enviados)
            perguntas_conteudo = request.POST.getlist('perguntas[]')
            tipos_pergunta = request.POST.getlist('tipos_pergunta[]')
            print("Tipos de Pergunta Recebidos:", request.POST.getlist('tipos_pergunta[]'))
            temas_pergunta = request.POST.getlist('temas[]')

            perguntas_atualizadas = []
            for i, conteudo in enumerate(perguntas_conteudo):
                pergunta_id = perguntas_ids_enviados[i] if i < len(perguntas_ids_enviados) else None
                tema_id = temas_pergunta[i] if i < len(temas_pergunta) else None
                tipo_input = tipos_pergunta[i] if i < len(temas_pergunta) else None

                try:
                    tipo_id = int(tipo_input)
                except ValueError:
                    tipo_pergunta = TipoPergunta.objects.get(descricao=tipo_input)
                    tipo_id = tipo_pergunta.id
                else:
                    tipo_pergunta = TipoPergunta.objects.get(id=tipo_id)

                tema_pergunta = TemaPergunta.objects.get(id=tema_id)

                if pergunta_id:
                    pergunta = Pergunta.objects.get(id=pergunta_id)
                else:
                    pergunta = Pergunta()
                
                pergunta.conteudo = conteudo
                pergunta.questionario = saved_questionario
                pergunta.tipo_pergunta = tipo_pergunta
                pergunta.tema = tema_pergunta
                pergunta.ano_letivo = anoletivo_id
                pergunta.save()
                perguntas_atualizadas.append(pergunta.id)

                if tipo_pergunta.descricao == 'escolha múltipla':
                    opcoes_ids_enviados = request.POST.getlist(f'opcoes_ids_{i}[]')
                    opcoes_textos = request.POST.getlist(f'opcoes_{i}[]')
                    print(f"Opções IDs recebidos para a pergunta {i+1}:", request.POST.getlist(f'opcoes_ids_{i}[]'))
                    print(f"Textos das opções recebidos para a pergunta {i+1}:", request.POST.getlist(f'opcoes_{i}[]'))


                    print(f"IDs de opções enviadas para pergunta {i+1}: {opcoes_ids_enviados}")
                    print(f"Textos das opções para pergunta {i+1}: {opcoes_textos}")

                    OpcaoEscolhaMultipla.objects.filter(pergunta=pergunta).exclude(id__in=opcoes_ids_enviados).delete()

                    for index, texto_opcao in enumerate(opcoes_textos):
                        opcao_id = opcoes_ids_enviados[index] if index < len(opcoes_ids_enviados) and opcoes_ids_enviados[index] else None
                        print(opcao_id)
                        if opcao_id:
                            opcao = OpcaoEscolhaMultipla.objects.get(id=opcao_id)
                        else:
                            opcao = OpcaoEscolhaMultipla(pergunta=pergunta)
                        opcao.texto_opcao = texto_opcao
                        opcao.save()




                elif tipo_pergunta.descricao == 'escalar':
                    valor_minimo = request.POST.get(f'valor_minimo[{i}]', None)
                    valor_maximo = request.POST.get(f'valor_maximo[{i}]', None)
                    if valor_minimo is not None:
                        pergunta.valor_minimo = int(valor_minimo)
                    if valor_maximo is not None:
                        pergunta.valor_maximo = int(valor_maximo)
                    pergunta.significado_minimo = request.POST.get(f'significado_minimo[{i}]', pergunta.significado_minimo)
                    pergunta.significado_maximo = request.POST.get(f'significado_maximo[{i}]', pergunta.significado_maximo)
                    pergunta.save()



            Pergunta.objects.filter(questionario=saved_questionario).exclude(id__in=perguntas_atualizadas).delete()

            if action == 'save_production':
                return redirect('main:mensagem', 26)
            elif action == 'save_created':
                return redirect('main:mensagem', 30)

    else:
        perguntas_ano_letivo = questionario.pergunta_set.first().ano_letivo if questionario.pergunta_set.exists() else None
        form = QuestionarioForm(instance=questionario, initial={
            'ano_letivo': perguntas_ano_letivo.id if perguntas_ano_letivo else None,
            'data_submissao': questionario.data_submissao.strftime('%Y-%m-%d') if questionario.data_submissao else None
        })

    perguntas_data = []
    perguntas = questionario.pergunta_set.all().prefetch_related('opcoes')
    for pergunta in perguntas:
        perguntas_dict = {
            'id': pergunta.id,
            'conteudo': pergunta.conteudo,
            'tipo': {
                'id': pergunta.tipo_pergunta.id,
                'descricao': pergunta.tipo_pergunta.descricao
            },
            'tema_id': pergunta.tema_id,
            'opcoes': [{'id': opcao.id, 'texto_opcao': opcao.texto_opcao} for opcao in pergunta.opcoes.all()]
        }
        if pergunta.tipo_pergunta.descricao == 'escalar':
            perguntas_dict.update({
                'valor_minimo': pergunta.valor_minimo,
                'significado_minimo': pergunta.significado_minimo,
                'valor_maximo': pergunta.valor_maximo,
                'significado_maximo': pergunta.significado_maximo,
            })
        perguntas_data.append(perguntas_dict)

    perguntas_json = json.dumps(perguntas_data, ensure_ascii=False)

    return render(request, 'questionarios/alterar_questionario.html', {
        'form': form,
        'questionario': questionario,
        'anos_letivos': anos_letivos,
        'temas': temas,
        'perguntas_json': perguntas_json,
        'temas_json': temas_json,
        'tipos_map_json': tipos_map_json,
    })


def confirmar_exclusao(request, questionario_id):
    questionario = get_object_or_404(Questionario, id=questionario_id)
    
    # Verifica se alguma pergunta do questionário tem respostas associadas
    perguntas = questionario.pergunta_set.all()
    respostas_existem = Resposta.objects.filter(Q(pergunta__in=perguntas)).exists()

    if respostas_existem:
        return redirect('main:mensagem', 43)
    
    if request.method == 'POST':
        if 'confirmar' in request.POST:
            questionario.delete()
            return redirect('questionarios:listar-questionarios')
        else:
            return redirect('questionarios:listar-questionarios')
    
    return render(request, 'questionarios/confirmar_exclusao.html', {'questionario_id': questionario_id})

@login_required
def responder_questionario(request, questionario_id):
    user = get_object_or_404(AuthUser, pk=request.user.id)  # Garantir que temos uma instância de AuthUser
    questionario = get_object_or_404(Questionario, pk=questionario_id, estado__estado='Publicado')
    
    # Verificar se o usuário já respondeu ao questionário
    ja_respondeu = RespostaUsuario.objects.filter(user=user, questionario=questionario).exists()

    perguntas = Pergunta.objects.filter(questionario=questionario).select_related('tema', 'tipo_pergunta').prefetch_related('opcoes').order_by('tema__tema')

    # Agrupar perguntas por tema
    perguntas_por_tema = defaultdict(list)
    for pergunta in perguntas:
        if pergunta.tipo_pergunta.descricao == "escalar":
            pergunta.escala_numeros = range(pergunta.valor_minimo, pergunta.valor_maximo + 1)
        tema_nome = pergunta.tema.tema if pergunta.tema else "Sem Tema"
        perguntas_por_tema[tema_nome].append(pergunta)

    if request.method == 'POST' and not ja_respondeu:
        todas_respondidas = True

        for pergunta in perguntas:
            if pergunta.tipo_pergunta.descricao in ['resposta curta', 'resposta longa']:
                resposta_conteudo = request.POST.get(f'resposta_{pergunta.id}', '').strip()
                if resposta_conteudo:
                    Resposta.objects.create(conteudo_string=resposta_conteudo, pergunta=pergunta)
                else:
                    todas_respondidas = False
                    break
            elif pergunta.tipo_pergunta.descricao == 'escolha múltipla':
                resposta_conteudo = request.POST.get(f'resposta_{pergunta.id}', '')
                if resposta_conteudo:
                    Resposta.objects.create(conteudo_string=resposta_conteudo, pergunta=pergunta)
                else:
                    todas_respondidas = False
                    break
            elif pergunta.tipo_pergunta.descricao == 'escalar':
                resposta_conteudo = request.POST.get(f'resposta_{pergunta.id}', None)
                if resposta_conteudo:
                    Resposta.objects.create(conteudo_int=int(resposta_conteudo), pergunta=pergunta)
                else:
                    todas_respondidas = False
                    break

        if todas_respondidas:
            # Registrar que o usuário respondeu ao questionario
            RespostaUsuario.objects.create(user=user, questionario=questionario)
            return redirect('main:mensagem', 29)

    return render(request, 'questionarios/responder_questionario.html', {
        'questionario': questionario,
        'perguntas_por_tema': dict(perguntas_por_tema),  # Converter para dicionário normal para o template
        'ja_respondeu': ja_respondeu
    })

def listar_questionarios_responder(request):
    questionarios_publicados = Questionario.objects.filter(estado__estado='Publicado')
    table = PublicadoTable(questionarios_publicados, request_user=request.user)
    tem_questionarios = questionarios_publicados.exists()
    return render(request, 'questionarios/listar_questionarios_responder.html', {'table': table, 'tem_questionarios': tem_questionarios})

def listar_estados_questionarios(request):
    descricao_filtro = request.GET.get('descricao', '')  # Obtém o valor do parâmetro de filtro da URL
    queryset = EstadoQuestionario.objects.all()
    if descricao_filtro:
        # Filtra pela descrição ou pelo estado
        queryset = queryset.filter(Q(descricao__icontains=descricao_filtro) | Q(estado__icontains=descricao_filtro))
    table = EstadoTable(queryset)
    return render(request, 'questionarios/listar_estados_questionarios.html', {'table': table})

def criar_estado_questionario(request):
    if request.method == 'POST':
        estado = request.POST.get('estado')
        descricao = request.POST.get('descricao')

        if EstadoQuestionario.objects.filter(estado=estado).exists():
            return redirect('main:mensagem', 31)
        elif EstadoQuestionario.objects.filter(descricao=descricao).exists():
            return redirect('main:mensagem', 32)
        
        novo_estado = EstadoQuestionario(estado=estado, descricao=descricao)
        novo_estado.save()
        return redirect('main:mensagem', 34)

    return render(request, 'questionarios/criar_estado_questionario.html')

def alterar_estado_questionario(request, pk):
    estado_atual = get_object_or_404(EstadoQuestionario, pk=pk)
    
    if request.method == 'POST':
        novo_estado = request.POST.get('estado')
        nova_descricao = request.POST.get('descricao')

        if EstadoQuestionario.objects.filter(estado=novo_estado).exclude(pk=pk).exists():
            return redirect('main:mensagem', 31)
        elif EstadoQuestionario.objects.filter(descricao=nova_descricao).exclude(pk=pk).exists():
            return redirect('main:mensagem', 32)
        
        if Questionario.objects.filter(estado=estado_atual).exists():
            return redirect('main:mensagem', 33)
        
        estado_atual.estado = novo_estado
        estado_atual.descricao = nova_descricao
        estado_atual.save()
        return redirect('main:mensagem', 35)

    return render(request, 'questionarios/alterar_estado_questionario.html', {'estado': estado_atual})

def eliminar_estado_questionario(request, pk):
    estado = get_object_or_404(EstadoQuestionario, pk=pk)
    
    if request.method == 'POST':
        if Questionario.objects.filter(estado=estado).exists():
            return redirect('main:mensagem', 33)

        estado.delete()
        return redirect('main:mensagem', 36)
    
    return render(request, 'questionarios/eliminar_estado_questionario.html', {'estado': estado})


def listar_temas_perguntas(request):
    descricao_filtro = request.GET.get('descricao', '')  # Obtém o valor do parâmetro de filtro da URL
    queryset = TemaPergunta.objects.all()

    if descricao_filtro:
        queryset = queryset.filter(Q(descricao__icontains=descricao_filtro) | Q(tema__icontains=descricao_filtro))

    table = TemaTable(queryset)
    return render(request, 'questionarios/listar_temas_perguntas.html', {'table': table})


def criar_tema_pergunta(request):
    if request.method == 'POST':
        tema = request.POST.get('tema')
        descricao = request.POST.get('descricao')
        
        if TemaPergunta.objects.filter(tema=tema).exists():
            return redirect('main:mensagem', 37)
        elif TemaPergunta.objects.filter(descricao=descricao).exists():
            return redirect('main:mensagem', 38)
        
        novo_tema = TemaPergunta(tema=tema, descricao=descricao)
        novo_tema.save()
        return redirect('main:mensagem', 39)
    return render(request, 'questionarios/criar_tema_pergunta.html')

def alterar_tema_pergunta(request, pk):
    tema = get_object_or_404(TemaPergunta, pk=pk)
    if request.method == 'POST':
        new_tema = request.POST.get('tema')
        descricao = request.POST.get('descricao')
        if not TemaPergunta.objects.filter(tema=new_tema).exclude(pk=pk).exists():
            tema.tema = new_tema
            tema.descricao = descricao
            tema.save()
        return redirect('questionarios:listar-temas-perguntas')
    return render(request, 'questionarios/alterar_tema_pergunta.html', {'tema': tema})

def eliminar_tema_pergunta(request, pk):
    tema = get_object_or_404(TemaPergunta, pk=pk)
    if request.method == 'POST':
        tema.delete()
        return redirect('questionarios:listar-temas-perguntas')
    return render(request, 'questionarios/eliminar_tema_pergunta.html', {'tema': tema})


def consultar_questionario(request, questionario_id):
    questionario = get_object_or_404(Questionario, id=questionario_id)
    perguntas = Pergunta.objects.filter(questionario_id=questionario_id)
    respostas = Resposta.objects.filter(pergunta__questionario_id=questionario_id)
    anos_letivos = questionario.q_ano_letivos.all()

    perguntas_por_tema = defaultdict(list)
    for pergunta in perguntas:
        if pergunta.tipo_pergunta.descricao == 'escolha múltipla':
            pergunta.options = OpcaoEscolhaMultipla.objects.filter(pergunta_id=pergunta.id)
        if pergunta.tipo_pergunta.descricao == "escalar":
            pergunta.escala_numeros = range(pergunta.valor_minimo, pergunta.valor_maximo + 1)
        tema_nome = pergunta.tema.tema if pergunta.tema else "Sem Tema"
        perguntas_por_tema[tema_nome].append(pergunta)

    return render(request, 'consultar_questionario.html', {
        'questionario': questionario,
        'perguntas_por_tema': dict(perguntas_por_tema), 
        'respostas': respostas,
        'anos_letivos': anos_letivos,
    })


def validar_questionario(request, questionario_id):
    questionario = get_object_or_404(Questionario, pk=questionario_id)
    perguntas = Pergunta.objects.filter(questionario_id=questionario_id)
    perguntas_por_tema = defaultdict(list)

    for pergunta in perguntas:
        if pergunta.tipo_pergunta.descricao == 'escolha múltipla':
            pergunta.options = OpcaoEscolhaMultipla.objects.filter(pergunta_id=pergunta.id)
        if pergunta.tipo_pergunta.descricao == "escalar":
            pergunta.escala_numeros = range(pergunta.valor_minimo, pergunta.valor_maximo + 1)
        tema_nome = pergunta.tema.tema if pergunta.tema else "Sem Tema"
        perguntas_por_tema[tema_nome].append(pergunta)

    if request.method == 'POST':
        acao_selecionada = request.POST.get('acao')
        comments = request.POST.get('comentarios')
        questionario = get_object_or_404(Questionario, pk=questionario_id)
        if acao_selecionada == 'A' and questionario.estado.id == 2:  # 2 estado "Criado"
            estado_validado = EstadoQuestionario.objects.get(id=3)  # 3 estado "Validado"
            questionario.estado = estado_validado
            questionario.data_validacao = timezone.now()
            questionario.save()
            return redirect('main:mensagem',28)
        elif acao_selecionada == 'R':
            estado_rejeitado = EstadoQuestionario.objects.get(id=4) # 4 estado "Rejeitado"
            questionario.estado = estado_rejeitado
            questionario.comentarios = comments
            questionario.save()
            return redirect('questionarios:listar-questionarios')
        elif acao_selecionada == 'DV':
            estado_anterior = EstadoQuestionario.objects.get(id=2) # 2 estado "Criado"
            questionario.estado = estado_anterior
            questionario.save()
            return redirect('questionarios:listar-questionarios')

    context = {
        'questionario': questionario,
        'perguntas_por_tema': dict(perguntas_por_tema),
        'data_submissao': questionario.data_submissao.strftime('%Y-%m-%d') if questionario.data_submissao else None
    }
    return render(request, 'questionarios/validar_questionario.html', context)


def publicar_questionario(request, questionario_id):
    questionario = get_object_or_404(Questionario, pk=questionario_id)
    estado_validado = EstadoQuestionario.objects.get(id=3)  # Estado "Validado"
    estado_publicado = EstadoQuestionario.objects.get(id=5)  # Estado "Publicado"

    if questionario.estado == estado_validado:
        ano_letivo_ativo = ano_letivo.objects.filter(ativo='S').first()

        if not ano_letivo_ativo:
            return render(request, 'error.html', {'message': 'Nenhum ano letivo ativo encontrado.'})

        perguntas_ano_letivo = Pergunta.objects.filter(ano_letivo=ano_letivo_ativo)
        questionarios_ano_letivo = Questionario.objects.filter(pergunta__in=perguntas_ano_letivo).distinct()

        if questionarios_ano_letivo.filter(estado=estado_publicado).exists():
            return redirect('main:mensagem', 40)

        perguntas = Pergunta.objects.filter(questionario=questionario)
        for pergunta in perguntas:
            if pergunta.ano_letivo != ano_letivo_ativo:
                return redirect('main:mensagem', 41)

        questionario.estado = estado_publicado
        questionario.save()

        ano_letivo_ativo.questionario_id = questionario.id
        ano_letivo_ativo.save()

        return redirect('main:mensagem', 42)

    elif questionario.estado == estado_publicado:
        questionario.estado = estado_validado
        questionario.save()

        ano_letivo_ativo = ano_letivo.objects.filter(ativo='S').first()
        if ano_letivo_ativo:
            ano_letivo_ativo.questionario_id = None
            ano_letivo_ativo.save()

        return redirect('questionarios:listar-questionarios')


def arquivar_questionario(request, questionario_id):
    questionario = get_object_or_404(Questionario, pk=questionario_id)
    estado_arquivado = get_object_or_404(EstadoQuestionario, id=7)  # ID do estado "Arquivado"

    # Armazenar o estado atual em uma variável temporária
    estado_anterior = questionario.estado

    if questionario.estado == estado_arquivado:
        # Se já estiver arquivado, reverter para o estado anterior
        if request.session.get(f'estado_anterior_{questionario_id}'):
            estado_anterior = EstadoQuestionario.objects.get(id=request.session[f'estado_anterior_{questionario_id}'])
            questionario.estado = estado_anterior
            del request.session[f'estado_anterior_{questionario_id}']
    else:
        # Se não estiver arquivado, arquivar e salvar o estado anterior na sessão
        request.session[f'estado_anterior_{questionario_id}'] = questionario.estado.id
        questionario.estado = estado_arquivado

    questionario.save()
    return redirect('questionarios:listar-questionarios')


def exportar_questionario_pdf(request, questionario_id):
    response = HttpResponse(content_type='application/pdf')
    response['Content-Disposition'] = f'attachment; filename="questionario_{questionario_id}.pdf"'

    p = canvas.Canvas(response)
    questionario = Questionario.objects.get(id=questionario_id)
    perguntas = Pergunta.objects.filter(questionario=questionario)

    # Cabeçalho do Questionário
    p.drawString(100, 735, f"Assunto: {questionario.assunto}")
    p.drawString(100, 720, "Perguntas:")

    y = 660  # Inicializar a posição y um pouco mais alto para mais espaço
    for pergunta in perguntas:
        tipo = pergunta.tipo_pergunta.descricao
        p.drawString(100, y, f"Tema: {pergunta.tema.tema}")
        y -= 20
        p.drawString(100, y, f"{pergunta.conteudo}")
        y -= 25  # Espaçamento aumentado após a pergunta

        if tipo == 'escolha múltipla':
            opcoes = pergunta.opcoes.all()
            for opcao in opcoes:
                p.drawString(120, y, f"[ ] {opcao.texto_opcao}")
                y -= 20  # Espaçamento entre opções
        elif tipo in ['resposta curta', 'resposta longa']:
            linhas = 1 if tipo == 'resposta curta' else 3
            for _ in range(linhas):
                p.line(120, y, 500, y)
                y -= 15
        elif tipo == 'escalar':
            p.drawString(120, y, f"Escala de {pergunta.valor_minimo} ({pergunta.significado_minimo}) a {pergunta.valor_maximo} ({pergunta.significado_maximo})")
            y -= 20
            p.drawString(120, y, "Resposta:")
            p.line(180, y, 300, y)  # Espaço para escrever um número
            y -= 25  # Espaçamento após a linha de resposta
        
        # Adicionar mais espaço entre perguntas diferentes
        y -= 30
        
        # Certifique-se de adicionar uma nova página se estiver correndo para fora do espaço
        if y < 50:
            p.showPage()
            y = 750

    p.showPage()
    p.save()
    return response


def exportar_todos_pdf(request):
    response = HttpResponse(content_type='application/pdf')
    response['Content-Disposition'] = 'attachment; filename="todos_questionarios.pdf"'

    p = canvas.Canvas(response)
    questionarios = Questionario.objects.all()

    y = 750  # Iniciar no topo da página
    for questionario in questionarios:
        p.drawString(100, y-15, f"Assunto: {questionario.assunto}")
        p.drawString(100, y-30, "Perguntas:")
        y -= 45  # Espaço antes de listar perguntas

        perguntas = Pergunta.objects.filter(questionario=questionario)
        for pergunta in perguntas:
            tipo = pergunta.tipo_pergunta.descricao
            p.drawString(100, y, f"Tema: {pergunta.tema.tema}")
            y -= 20
            p.drawString(100, y, f"{pergunta.conteudo}")
            y -= 25  # Espaçamento após a pergunta

            if tipo == 'escolha múltipla':
                opcoes = pergunta.opcoes.all()
                for opcao in opcoes:
                    p.drawString(120, y, f"[ ] {opcao.texto_opcao}")
                    y -= 20
            elif tipo in ['resposta curta', 'resposta longa']:
                linhas = 1 if tipo == 'resposta curta' else 3
                for _ in range(linhas):
                    p.line(120, y, 500, y)
                    y -= 15
            elif tipo == 'escalar':
                p.drawString(120, y, f"Escala de {pergunta.valor_minimo} ({pergunta.significado_minimo}) a {pergunta.valor_maximo} ({pergunta.significado_maximo})")
                y -= 20
                p.drawString(120, y, "Resposta:")
                p.line(180, y, 300, y)
                y -= 25
            
            y -= 30  # Espaçamento adicional entre perguntas diferentes

            # Verificar se é necessário adicionar uma nova página
            if y < 50:
                p.showPage()
                y = 750
        
        y -= 50  # Espaçamento adicional entre diferentes questionários
        if y < 50:
            p.showPage()
            y = 750

    p.showPage()
    p.save()
    return response
