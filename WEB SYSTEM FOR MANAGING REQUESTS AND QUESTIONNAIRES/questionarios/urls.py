from django.urls import path
from .views import eliminar_tema_pergunta, alterar_tema_pergunta, criar_tema_pergunta, listar_questionarios_responder, listar_temas_perguntas, eliminar_estado_questionario, alterar_estado_questionario, criar_estado_questionario, listar_estados_questionarios, listar_questionarios,criar_questionario,confirmar_exclusao, consultar_questionario, responder_questionario, validar_questionario, arquivar_questionario, alterar_questionario, exportar_questionario_pdf, publicar_questionario, exportar_todos_pdf

app_name = "questionarios"

urlpatterns = [
    path('questionarios', listar_questionarios, name='listar-questionarios'),
    path('listar_questionarios', listar_questionarios, name='listar-questionarios'),
    path('criar_questionario/', criar_questionario, name='criar-questionario'),
    path('consultar_questionario/<int:questionario_id>', consultar_questionario, name='consultar-questionario'),
    path('validar_questionario/<int:questionario_id>', validar_questionario, name='validar-questionario'),
    path('questionario/<int:questionario_id>/arquivar/', arquivar_questionario, name='arquivar-questionario'),
    path('questionario/<int:questionario_id>/alterar/', alterar_questionario, name='alterar-questionario'),
    path('questionario/<int:questionario_id>/publicar/', publicar_questionario, name='publicar-questionario'),
    path('questionario/<int:questionario_id>/responder/', responder_questionario, name='responder-questionario'),
    path('exportar/pdf/<int:questionario_id>/', exportar_questionario_pdf, name='exportar-pdf'),
    path('exportar/todos/pdf/', exportar_todos_pdf, name='exportar-todos-pdf'),
    path('confirmar_exclusao/<int:questionario_id>/', confirmar_exclusao, name='confirmar-exclusao'),
    path('estados/', listar_estados_questionarios, name='listar-estados-questionarios'),
    path('estado/novo/', criar_estado_questionario, name='criar-estado-questionario'),
    path('estado/editar/<int:pk>/', alterar_estado_questionario, name='alterar-estado-questionario'),
    path('estado/eliminar/<int:pk>/', eliminar_estado_questionario, name='eliminar-estado-questionario'),
    path('temas/', listar_temas_perguntas, name='listar-temas-perguntas'),
    path('tema/novo/', criar_tema_pergunta, name='criar-tema-pergunta'),
    path('tema/editar/<int:pk>/', alterar_tema_pergunta, name='alterar-tema-pergunta'),
    path('tema/eliminar/<int:pk>/', eliminar_tema_pergunta, name='eliminar-tema-pergunta'),
    path('questionarios-publicados/', listar_questionarios_responder, name='listar-questionarios-responder'),
]
