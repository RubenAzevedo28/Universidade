from django.urls import reverse
import django_tables2 as tables
from django_tables2.utils import A

from main.models import AuthUser
from .models import EstadoQuestionario, Questionario, UtilizadorQuestionario, TemaPergunta, Pergunta
from django.utils.html import format_html

class QuestionarioTable(tables.Table):
    estado = tables.Column(accessor='estado.estado', verbose_name='Estado')
    user_info = tables.Column(empty_values=(), verbose_name='Criador')
    acoes = tables.Column(empty_values=(), orderable=False, attrs={"th": {"width": "150"}})

    def __init__(self, *args, **kwargs):
        self.request_user = kwargs.pop('request_user', None)
        super().__init__(*args, **kwargs)

    def render_user_info(self, record):
        utilizador_questionarios = UtilizadorQuestionario.objects.filter(questionario_id=record.id).values_list('utilizadoruser_ptr_id', flat=True)
        if utilizador_questionarios:
            user_infos = AuthUser.objects.filter(id=utilizador_questionarios[0])
            user_info = ", ".join([user.username for user in user_infos])
            return format_html(user_info)
        else:
            return format_html("No data available")


    def render_acoes(self, record):
        base_style = "display: table-cell; text-align: center; vertical-align: middle;"
        validar = ""
        if self.request_user.groups.filter(name="PCP").exists() and record.estado.id == 2:
            validar = f"<span style='{base_style}'><a href='{reverse('questionarios:validar-questionario', args=[record.id])}' class='has-text-success'><i class='mdi mdi-check mdi-24px'></i></a></span>"
        if self.request_user.groups.filter(name="PCP").exists() and record.estado.id == 3:
            validar = f"<span style='{base_style}'><a href='{reverse('questionarios:validar-questionario', args=[record.id])}' class='has-text-danger'><i class='mdi mdi-replay mdi-24px'></i></a></span>"
        
        publicar = ""
        if self.request_user.groups.filter(name="PCP").exists() and record.estado.id == 3:
            publicar = f"<span style='{base_style}'><a href='{reverse('questionarios:publicar-questionario', args=[record.id])}'><i class='mdi mdi-publish mdi-24px'></i></a></span>"
        if self.request_user.groups.filter(name="PCP").exists() and record.estado.id == 5:
            publicar = f"<span style='{base_style}'><a href='{reverse('questionarios:publicar-questionario', args=[record.id])}' class='has-text-danger'><i class='mdi mdi-replay mdi-24px'></i></a></span>"
        
        
        arquivar = f"<span style='{base_style}'><a href='{reverse('questionarios:arquivar-questionario', args=[record.id])}'><i class='mdi mdi-archive mdi-24px'></i></a></span>"
        consultar = f"<span style='{base_style}'><a href='{reverse('questionarios:consultar-questionario', args=[record.id])}'><i class='mdi mdi-eye mdi-24px'></i></a></span>"
        is_criador = UtilizadorQuestionario.objects.filter(utilizadoruser_ptr_id=self.request_user.id, questionario=record).exists()

        if is_criador and record.estado.id != 5:
            eliminar = f"<span style='{base_style}'><a href='{reverse('questionarios:confirmar-exclusao', args=[record.id])}' class='has-text-danger'><i class='mdi mdi-trash-can mdi-24px'></i></a></span>"
        else:
            eliminar = ""

        if is_criador:
            alterar = f"<span style='{base_style}'><a href='{reverse('questionarios:alterar-questionario', args=[record.id])}'><i class='mdi mdi-pencil mdi-24px'></i></a></span>"
        else:
            alterar = ""

        exportar_pdf = f"<span style='{base_style}'><a href='{reverse('questionarios:exportar-pdf', args=[record.id])}'><i class='fas fa-file-pdf'></i></a></span>"
        container_style = "display: table; width: 100%; table-layout: fixed;"
        return format_html(f"<div style='{container_style}'>{validar}{publicar}{consultar}{eliminar}{alterar}{arquivar}{exportar_pdf}</div>")

    def render_estado(self, value, record):
        if record.estado.estado == 'Validado':
            color = '#00FF00'  # green
        elif record.estado.estado == 'Publicado':
            color = '#66FF66'  # green
        elif record.estado.estado == 'Criado':
            color = '#8B8000'  # yellow
        elif record.estado.estado == 'Em Produção':
            color = '#FFA500'  # yellow
        elif record.estado.estado == 'Arquivado':
            color = '#FFAAAA'  # yellow
        elif record.estado.estado == 'Rejeitado':
            color = '#FF0000'  # red
        elif record.estado.estado == 'Finalizado':
            color = '#000000'  # black
        else:
            color = '#000000'  # default no background color

        return format_html(f"<div style='color: {color}; text-align: center;'>{value}</div>")

    class Meta:
        model = Questionario
        template_name = "django_tables2/bootstrap.html"
        fields = ('assunto', 'estado', 'data_validacao', 'user_info')

class EstadoTable(tables.Table):
    estado = tables.Column()
    descricao = tables.Column()
    acoes = tables.Column(empty_values=(), orderable=False)

    def render_acoes(self, record):
        editar = f'<a href="{reverse("questionarios:alterar-estado-questionario", args=[record.id])}"><i class="mdi mdi-pencil"></i></a>'
        eliminar = f'<a href="{reverse("questionarios:eliminar-estado-questionario", args=[record.id])}" class="has-text-danger"><i class="mdi mdi-trash-can"></i></a>'
        return format_html(f'{editar} {eliminar}')

    class Meta:
        model = EstadoQuestionario
        template_name = "django_tables2/bootstrap.html"
        fields = ("estado", "descricao")

class TemaTable(tables.Table):
    tema = tables.Column()
    descricao = tables.Column()
    acoes = tables.Column(empty_values=(), orderable=False)

    def render_acoes(self, record):
        has_perguntas = Pergunta.objects.filter(tema=record).exists()
        
        editar = f'<a href="{reverse("questionarios:alterar-tema-pergunta", args=[record.id])}"><i class="mdi mdi-pencil"></i></a>'
        if has_perguntas:
            editar = ''
        eliminar = f'<a href="{reverse("questionarios:eliminar-tema-pergunta", args=[record.id])}" class="has-text-danger"><i class="mdi mdi-trash-can"></i></a>'
        return format_html(f'{editar} {eliminar}')

    class Meta:
        model = TemaPergunta
        template_name = "django_tables2/bootstrap.html"
        fields = ("tema", "descricao")

class PublicadoTable(tables.Table):
    estado = tables.Column(accessor='estado.estado', verbose_name='Estado')
    user_info = tables.Column(empty_values=(), verbose_name='Criador')
    acoes = tables.Column(empty_values=(), orderable=False, attrs={"th": {"width": "150"}})

    def __init__(self, *args, **kwargs):
        self.request_user = kwargs.pop('request_user', None)
        super().__init__(*args, **kwargs)

    def render_user_info(self, record):
        utilizador_questionarios = UtilizadorQuestionario.objects.filter(questionario_id=record.id).values_list('utilizadoruser_ptr_id', flat=True)
        if utilizador_questionarios:
            user_infos = AuthUser.objects.filter(id=utilizador_questionarios[0])
            user_info = ", ".join([user.username for user in user_infos])
            return format_html(user_info)
        else:
            return format_html("No data available")

    def render_acoes(self, record):
        base_style = "display: table-cell; text-align: center; vertical-align: middle;"
        responder = f"<span style='{base_style}'><a href='{reverse('questionarios:responder-questionario', args=[record.id])}' class='has-text-primary'><i class='mdi mdi-comment-question mdi-24px'></i></a></span>"

        container_style = "display: table; width: 100%; table-layout: fixed;"
        return format_html(f"<div style='{container_style}'>{responder}</div>")

    def render_estado(self, value, record):
        if record.estado.estado == 'Publicado':
            color = '#66FF66'  # green
        else:
            color = '#000000'  # default no background color

        return format_html(f"<div style='color: {color}; text-align: center;'>{value}</div>")

    class Meta:
        model = Questionario
        template_name = "django_tables2/bootstrap.html"
        fields = ('assunto', 'estado', 'data_validacao', 'user_info')

    @classmethod
    def get_queryset(cls):
        return Questionario.objects.filter(estado__estado='Publicado')