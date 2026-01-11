from django import forms
from .models import Questionario
from anoletivo.models import ano_letivo

class QuestionarioForm(forms.ModelForm):
    ano_letivo = forms.ModelChoiceField(queryset=ano_letivo.objects.all(), required=True, empty_label="Selecione o Ano Letivo", label="Ano Letivo", widget=forms.Select(attrs={'class': 'select'}))

    class Meta:
        model = Questionario
        exclude = ['estado', 'data_criacao', 'data_submissao', 'data_validacao', 'anoletivoid']  
        widgets = {
            'descricao': forms.TextInput(attrs={'class': 'input', 'placeholder': 'Descrição'}),
            'assunto': forms.TextInput(attrs={'class': 'input', 'placeholder': 'Assunto'}),
        }

