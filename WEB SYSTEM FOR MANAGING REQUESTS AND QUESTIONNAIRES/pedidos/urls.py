"""gestaopedidos URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.urls import path
from django.conf.urls.static import static

from gestaodepedidos import settings
from .views import criar_pedidos_outros,editar_pedidos_outros,eliminar_pedido_outros,consultar_pedido_outros
from .views import listar_pedidos
from .views import criar_pedidos_sala,editar_pedido_sala, eliminar_pedido_sala, consultar_pedido_sala
from .views import criar_pedidos_uc,editar_pedidos_uc,eliminar_pedido_uc,consultar_pedidos_uc
from .views import criar_pedidos_horario,editar_pedidos_horario,eliminar_pedido_horario,consultar_pedido_horario
from .views import criar_pedidos_aluno,editar_pedidos_aluno,eliminar_pedido_aluno,consultar_pedido_aluno
from .views import criar_pedidos_parecer,editar_pedidos_parecer,eliminar_pedido_parecer,consultar_pedido_parecer
from .views import exportar_todos_pedidos_pdf,  exportar_todos_pedidos_csv, exportar_pedidos_pdf,exportar_pedidos_csv,exportar_pedidos
from .views import mensagem
from .views import associar_pedido_funcionario,validar_pedido,desassociar_pedido_funcionario
from .views import consultar_pedidos
from .views import exportar_pedidos_table
from .views import obter_informacao

app_name = "pedidos"

urlpatterns = [

    path('listagempedidos', consultar_pedidos.as_view(),name='listagem-pedidos'),
    #path('exportarpedidos', exportar_pedidos_table.as_view(),name='exportar-pedidos'),
    path('obterinformacao/<int:id>', obter_informacao, name='obter-informacao'),


    path('listarpedidos', listar_pedidos, name='listar-pedidos'),
    path('exportarpedidos', exportar_pedidos, name='exportar-pedidos'),
    path('exportarpedidospdf/<int:id>', exportar_pedidos_pdf, name='exportar-pedidos-pdf'),
    path('exportarpedidoscsv/<int:id>', exportar_pedidos_csv, name='exportar-pedidos-csv'),
    path('exportartodospedidospdf', exportar_todos_pedidos_pdf, name='exportar-todos-pedidos-pdf'),
    path('exportartodospedidoscsv', exportar_todos_pedidos_csv, name='exportar-todos-pedidos-csv'),

    path('consultarpedidooutros/<int:id>', consultar_pedido_outros, name='consultar-pedido-outros'),
    path('criarpedidooutros', criar_pedidos_outros, name='criar-pedido-outros'),
    path('editarpedidooutros/<int:id>', editar_pedidos_outros, name='editar-pedido-outros'),
    path('eliminarpedidooutros/<int:id>', eliminar_pedido_outros, name='eliminar-pedido-outros'),

    path('criarpedidosala', criar_pedidos_sala, name='criar-pedido-sala'),
    path('editarpedidosala/<int:id>', editar_pedido_sala, name='editar-pedido-sala'),
    path('eliminarpedidosala/<int:id>', eliminar_pedido_sala, name='eliminar-pedido-sala'),
    path('consultarpedidosala/<int:id>', consultar_pedido_sala, name='consultar-pedido-sala'),

    path('consultarpedidosuc/<int:id>', consultar_pedidos_uc, name='consultar-pedido-uc'),
    path('criarpedidouc', criar_pedidos_uc, name='criar-pedido-uc'),
    path('editarpedidouc/<int:id>', editar_pedidos_uc, name='editar-pedido-uc'),
    path('eliminarpedidouc/<int:id>', eliminar_pedido_uc, name='eliminar-pedido-uc'),

    path('consultarpedidohorario/<int:id>', consultar_pedido_horario, name='consultar-pedido-horario'),
    path('criarpedidohorario', criar_pedidos_horario, name='criar-pedido-horario'),
    path('editarpedidohorario/<int:id>', editar_pedidos_horario, name='editar-pedido-horario'),
    path('eliminarpedidohorario/<int:id>', eliminar_pedido_horario, name='eliminar-pedido-horario'),

    path('consultarpedidoparecer/<int:id>', consultar_pedido_parecer, name='consultar-pedido-parecer'),
    path('criarpedidoparecer', criar_pedidos_parecer, name='criar-pedido-parecer'),
    path('editarpedidoparecer/<int:id>', editar_pedidos_parecer, name='editar-pedido-parecer'),
    path('eliminarpedidoparecer/<int:id>', eliminar_pedido_parecer, name='eliminar-pedido-parecer'),

    path('consultarpedidoaluno/<int:id>', consultar_pedido_aluno, name='consultar-pedido-aluno'),
    path('criarpedidoaluno', criar_pedidos_aluno, name='criar-pedido-aluno'),
    path('editarpedidoaluno/<int:id>', editar_pedidos_aluno, name='editar-pedido-aluno'),
    path('eliminarpedidoaluno/<int:id>', eliminar_pedido_aluno, name='eliminar-pedido-aluno'),

    path('desassociarpedidofuncionario/<int:id>', desassociar_pedido_funcionario, name='desassociar-pedido-funcionario'),

    path('associarpedidofuncionario/<int:id>', associar_pedido_funcionario, name='associar-pedido-funcionario'),
    path('validarpedido/<int:id>', validar_pedido, name='validar-pedido'),
    path('mensagem/<int:id>', mensagem,name='mensagem'),

] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)

