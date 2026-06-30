{{- define "imdg-chatbot.name" -}}
{{- default .Chart.Name .Values.nameOverride -}}
{{- end -}}

{{- define "imdg-chatbot.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride -}}
{{- else -}}
{{- include "imdg-chatbot.name" . -}}
{{- end -}}
{{- end -}}
