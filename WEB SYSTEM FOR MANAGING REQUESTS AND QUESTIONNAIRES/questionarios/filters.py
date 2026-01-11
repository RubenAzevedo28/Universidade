import django_filters
from .models import EstadoQuestionario, Questionario
from django.db.models import Q

from anoletivo.models import ano_letivo

def get_estado_choice():
    return EstadoQuestionario.objects.all().values_list('id', 'estado')

def get_anoletivoid_choice():
    return ano_letivo.objects.all().values_list('id', 'ano_letivo')

class QuestionarioFilter(django_filters.FilterSet):    
    estado = django_filters.MultipleChoiceFilter(choices=get_estado_choice())
    anoletivoid = django_filters.ModelMultipleChoiceFilter(
        queryset=ano_letivo.objects.all(),
        field_name='q_ano_letivos',  # Using the related_name for reverse lookup
        label='Ano Letivo'
    )

    class Meta:
        model = Questionario
        fields = '__all__'  # Make sure only relevant fields are exposed
