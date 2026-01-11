import django_filters
from django_filters import rest_framework as filters
from questionarios.models import Pergunta, TipoPergunta, ano_letivo

def get_tipo_pergunta_choices():
    return TipoPergunta.objects.all().values_list('id', 'descricao')

def get_ano_letivo_choices():
    return ano_letivo.objects.all().values_list('id', 'ano_letivo')

class PerguntaFilter(django_filters.FilterSet):
    tipo_pergunta = django_filters.MultipleChoiceFilter(choices=get_tipo_pergunta_choices())
    ano_letivo = django_filters.MultipleChoiceFilter(choices=get_ano_letivo_choices())

    class Meta:
        model = Pergunta
        fields = ['tipo_pergunta', 'ano_letivo']