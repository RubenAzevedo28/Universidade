from django.shortcuts import render
from markupsafe import soft_str
from main.models import Docente
from pedidos.models import Categoria, PedidoDeSala, Sala, Pedido, TipoDePedido, subpedido_sala, EstadoPedido
from django.db.models import Q
from django.utils.dateparse import parse_datetime

import csv
from django.http import HttpResponse
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas

from reportlab.lib.pagesizes import landscape, A4
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet

def criar_relatorio_salas(request):
    data_inicio = request.GET.get('data_inicio')
    data_fim = request.GET.get('data_fim')

    salas_selecionadas = Sala.objects.all()
    tipos_salas_selecionadas = Categoria.objects.all()
    relatorio = []

    filtro_data = Q()
    if data_inicio and data_fim:
        filtro_data = Q(inicio__gte=parse_datetime(data_inicio), fim__lte=parse_datetime(data_fim))

    # Relatório por Sala Específica
    for sala in salas_selecionadas:
        subpedidos_sala = subpedido_sala.objects.filter(id_sala=sala.id).filter(filtro_data)
        quantidade_de_subpedidos_associados = subpedidos_sala.count()

        if quantidade_de_subpedidos_associados > 0:
            pedido_ids_sala = subpedidos_sala.values_list('pedido_ptr_id', flat=True).distinct()
            pedidos_associados_sala = Pedido.objects.filter(id__in=pedido_ids_sala)
            
            quantidade_de_pedidos_associados = pedidos_associados_sala.count()
            
            pendentes = pedidos_associados_sala.filter(estado_0_id=1).count()
            em_analise = pedidos_associados_sala.filter(estado_0_id=2).count()
            aceites = pedidos_associados_sala.filter(estado_0_id=3).count()
            rejeitados = pedidos_associados_sala.filter(estado_0_id=4).count()
            
            relatorio.append({
                'sala': sala.descricao_sala,
                'quantidade_de_subpedidos_associados': quantidade_de_subpedidos_associados,
                'quantidade_de_pedidos_associados': quantidade_de_pedidos_associados,
                'pendentes': pendentes,
                'em_analise': em_analise,
                'aceites': aceites,
                'rejeitados': rejeitados,
            })

    # Relatório por Tipo de Sala
    for tipo_sala in tipos_salas_selecionadas:
        subpedidos_tipo = subpedido_sala.objects.filter(categoria=tipo_sala.tipo_de_sala).filter(filtro_data)
        quantidade_de_subpedidos_associados = subpedidos_tipo.count()
        
        if quantidade_de_subpedidos_associados > 0:
            pedido_ids_tipo = subpedidos_tipo.values_list('pedido_ptr_id', flat=True).distinct()
            pedidos_associados_tipo = Pedido.objects.filter(tipo_id=3).filter(id__in=pedido_ids_tipo)

            quantidade_de_pedidos_associados = pedidos_associados_tipo.count()

            pendentes = pedidos_associados_tipo.filter(estado_0_id=1).count()
            em_analise = pedidos_associados_tipo.filter(estado_0_id=2).count()
            aceites = pedidos_associados_tipo.filter(estado_0_id=3).count()
            rejeitados = pedidos_associados_tipo.filter(estado_0_id=4).count()

            relatorio.append({
                'sala': tipo_sala.tipo_de_sala,
                'quantidade_de_subpedidos_associados': quantidade_de_subpedidos_associados,
                'quantidade_de_pedidos_associados': quantidade_de_pedidos_associados,
                'pendentes': pendentes,
                'em_analise': em_analise,
                'aceites': aceites,
                'rejeitados': rejeitados,
        })

    return render(request, 'relatorios/criar_relatorio_salas.html', {'relatorio': relatorio})

def criar_relatorio_pedidos(request):
    data_inicio = request.GET.get('data_inicio')
    data_fim = request.GET.get('data_fim')

    pedidos_selecionados = Pedido.objects.all()
    pedidos_relatorio = []

    filtro_data = Q()
    if data_inicio and data_fim:
        filtro_data = Q(data_de_submissao__gte=parse_datetime(data_inicio)) & Q(data_de_alvo__lte=parse_datetime(data_fim))

    for pedido in pedidos_selecionados.filter(filtro_data):
        estado = pedido.estado_0.estado if pedido.estado_0 else 'N/A'
        tipo_pedido = pedido.tipo.tipo if pedido.tipo else 'N/A'
        proponente = f"{pedido.docenteutilizadorid.first_name} {pedido.docenteutilizadorid.last_name} ({pedido.docenteutilizadorid.email})" if pedido.docenteutilizadorid else 'N/A'
        data_validacao = pedido.data_de_validacao if pedido.data_de_validacao else 'N/A'
        pedidos_relatorio.append({
            'id': pedido.id,
            'data_de_submissao': pedido.data_de_submissao,
            'data_de_alvo': pedido.data_de_alvo,
            'estado': estado,
            'tipo_pedido': tipo_pedido,
            'proponente': proponente,
            'data_validacao': data_validacao,
            'assunto': pedido.assunto,
            'informacoes': pedido.informacoes,
            'comentarios': pedido.comentarios
        })

    return render(request, 'relatorios/criar_relatorio_pedidos.html', {'pedidos': pedidos_relatorio})

def criar_relatorio_docentes(request):
    ativo = request.GET.get('ativo')

    docentes_selecionados = Docente.objects.all()
    docentes_relatorio = []

    if ativo == 'S':
        docentes_selecionados = docentes_selecionados.filter(ativo='S')
    elif ativo == 'N':
        docentes_selecionados = docentes_selecionados.filter(Q(ativo='N') | Q(ativo__isnull=True))

    for docente in docentes_selecionados:
        docentes_relatorio.append({
            'nome': docente.nome,
            'email': docente.email,
            'codigo': docente.codigo,
            'ativo': docente.ativo,
            'data_nascimento': docente.data_de_nascimento,
            'nif': docente.nif
        })

    return render(request, 'relatorios/criar_relatorio_docente.html', {'docentes': docentes_relatorio})

def exportar_pedidos_csv(request):
    data_inicio = request.GET.get('data_inicio')
    data_fim = request.GET.get('data_fim')

    filtro_data = Q()
    if data_inicio and data_fim:
        filtro_data = Q(data_de_submissao__gte=parse_datetime(data_inicio)) & Q(data_de_alvo__lte=parse_datetime(data_fim))

    pedidos = Pedido.objects.filter(filtro_data)

    response = HttpResponse(content_type='text/csv')
    response['Content-Disposition'] = 'attachment; filename="relatorio_pedidos.csv"'
    response.write(u'\ufeff'.encode('utf8')) 

    writer = csv.writer(response, delimiter=';', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    writer.writerow(['Assunto', 'Informações', 'Data Alvo', 'Estado', 'Data de Submissão', 'Data de Validação', 'Tipo de Pedido', 'Proponente', 'Comentários'])

    for pedido in pedidos:
        estado = pedido.estado_0.estado if pedido.estado_0 else 'N/A'
        tipo_pedido = pedido.tipo.tipo if pedido.tipo else 'N/A'
        proponente = f"{pedido.docenteutilizadorid.first_name} {pedido.docenteutilizadorid.last_name} ({pedido.docenteutilizadorid.email})" if pedido.docenteutilizadorid else 'N/A'
        data_validacao = pedido.data_de_validacao.strftime("%d/%m/%Y") if pedido.data_de_validacao else 'N/A'
        writer.writerow([
            pedido.assunto,
            pedido.informacoes,
            pedido.data_de_alvo.strftime("%d/%m/%Y") if pedido.data_de_alvo else 'N/A',
            estado,
            pedido.data_de_submissao.strftime("%d/%m/%Y") if pedido.data_de_submissao else 'N/A',
            data_validacao,
            tipo_pedido,
            proponente,
            pedido.comentarios if pedido.comentarios else 'N/A'
        ])

    return response

def exportar_pedidos_pdf(request):
    data_inicio = request.GET.get('data_inicio')
    data_fim = request.GET.get('data_fim')

    filtro_data = Q()
    if data_inicio and data_fim:
        filtro_data = Q(data_de_submissao__gte=parse_datetime(data_inicio)) & Q(data_de_alvo__lte=parse_datetime(data_fim))

    pedidos = Pedido.objects.filter(filtro_data)

    response = HttpResponse(content_type='application/pdf')
    response['Content-Disposition'] = 'attachment; filename="relatorio_pedidos.pdf"'

    doc = SimpleDocTemplate(response, pagesize=landscape(A4))
    elements = []

    styles = getSampleStyleSheet()
    title = Paragraph("Relatório de Pedidos", styles['Title'])
    elements.append(title)
    elements.append(Spacer(1, 12))

    # Adicionar datas dos filtros
    filtro_data_text = f"Filtros: Data Início - {data_inicio} | Data Fim - {data_fim}"
    filtro_paragraph = Paragraph(filtro_data_text, styles['Normal'])
    elements.append(filtro_paragraph)
    elements.append(Spacer(1, 12))

    data = [
        ['Assunto', 'Informações', 'Data Alvo', 'Estado', 'Data de Submissão', 'Data de Validação', 'Tipo de Pedido', 'Proponente']
    ]

    for pedido in pedidos:
        estado = pedido.estado_0.estado if pedido.estado_0 else 'N/A'
        tipo_pedido = pedido.tipo.tipo if pedido.tipo else 'N/A'
        proponente = f"{pedido.docenteutilizadorid.first_name} {pedido.docenteutilizadorid.last_name}" if pedido.docenteutilizadorid else 'N/A'
        data_validacao = pedido.data_de_validacao.strftime("%d/%m/%Y") if pedido.data_de_validacao else 'N/A'
        data.append([
            pedido.assunto,
            pedido.informacoes,
            pedido.data_de_alvo.strftime("%d/%m/%Y") if pedido.data_de_alvo else 'N/A',
            estado,
            pedido.data_de_submissao.strftime("%d/%m/%Y") if pedido.data_de_submissao else 'N/A',
            data_validacao,
            tipo_pedido,
            proponente,
        ])

    col_widths = [1.7 * inch, 2.5 * inch, 1.0 * inch, 1.0 * inch, 1.5 * inch, 1.4 * inch, 1.2 * inch, 1.3 * inch]
    table = Table(data, colWidths=col_widths)
    table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.grey),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
        ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
        ('FONTSIZE', (0, 0), (-1, 0), 10),
        ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
        ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
        ('GRID', (0, 0), (-1, -1), 1, colors.black),
    ]))

    elements.append(table)
    doc.build(elements)

    return response


def exportar_docentes_pdf(request):
    ativo = request.GET.get('ativo')
    
    docentes_selecionados = Docente.objects.all()

    if ativo == 'S':
        docentes_selecionados = docentes_selecionados.filter(ativo='S')
    elif ativo == 'N':
        docentes_selecionados = docentes_selecionados.filter(Q(ativo='N') | Q(ativo__isnull=True))
    
    response = HttpResponse(content_type='application/pdf')
    response['Content-Disposition'] = 'attachment; filename="relatorio_docentes.pdf"'

    doc = SimpleDocTemplate(response, pagesize=landscape(A4))
    styles = getSampleStyleSheet()
    elements = []

    title = Paragraph("Relatório de Docentes", styles['Title'])
    elements.append(title)
    elements.append(Spacer(1, 12))

    data = [['Nome', 'Email', 'Código', 'Ativo', 'Data de Nascimento', 'NIF']]

    for docente in docentes_selecionados:
        data.append([
            docente.nome,
            docente.email,
            docente.codigo,
            docente.ativo,
            docente.data_de_nascimento.strftime("%Y-%m-%d") if docente.data_de_nascimento else '',
            docente.nif
        ])

    table = Table(data)
    table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.grey),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
        ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
        ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
        ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
        ('GRID', (0, 0), (-1, -1), 1, colors.black),
    ]))

    elements.append(table)
    doc.build(elements)

    return response

def exportar_docentes_csv(request):
    ativo = request.GET.get('ativo')
    
    docentes_selecionados = Docente.objects.all()

    if ativo == 'S':
        docentes_selecionados = docentes_selecionados.filter(ativo='S')
    elif ativo == 'N':
        docentes_selecionados = docentes_selecionados.filter(Q(ativo='N') | Q(ativo__isnull=True))
    
    response = HttpResponse(content_type='text/csv')
    response['Content-Disposition'] = 'attachment; filename="relatorio_docentes.csv"'
    response.write(u'\ufeff'.encode('utf8'))  # Adiciona BOM para indicar UTF-8

    writer = csv.writer(response, delimiter=';', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    writer.writerow([soft_str(u'Nome'), soft_str(u'Email'), soft_str(u'Código'), soft_str(u'Ativo'), soft_str(u'Data de Nascimento'), soft_str(u'NIF')])

    for docente in docentes_selecionados:
        writer.writerow([
            soft_str(docente.nome),
            soft_str(docente.email),
            soft_str(docente.codigo),
            soft_str(docente.ativo),
            soft_str(docente.data_de_nascimento.strftime("%Y-%m-%d") if docente.data_de_nascimento else ''),
            soft_str(docente.nif)
        ])

    return response