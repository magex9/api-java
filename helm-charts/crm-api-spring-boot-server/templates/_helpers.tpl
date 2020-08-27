{{/*
Expand the name of the chart.
*/}}
{{- define "crm-api-spring-boot-server.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "crm-api-spring-boot-server.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Get application version from either the chart name or the parameters

*/}}
{{- define "crm-api-spring-boot-server.appVersion" -}}
{{- default .Chart.AppVersion .Values.application.version }}
{{- end }}
{{/* 
determine image name (with repo, tags and app name}
*/}}

{{- define "crm-api-spring-boot-server.image" -}}
  {{- if .Values.application.group }}
    {{- printf "%s/%s/%s:%s" .Values.image.repository .Values.application.group .Chart.Name (include "crm-api-spring-boot-server.appVersion" .) }}
  {{- else }}
    {{- printf "%s/%s:%s" .Values.image.repository .Chart.Name (include "crm-api-spring-boot-server.appVersion" .) }}
  {{- end }}
{{- end }}



{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "crm-api-spring-boot-server.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Populate ingress hostname like : {app_name}-{namespace}.{domain}  if the host.name is empty
This is running inside a ingress.hosts scope, then the the root is now .Values.ingress.hosts
ex: the value .Values.ingress.hosts.host becomes only . in this loop
in order to get the root scope , we need to preprend variable with a $ , like $.Release.Name . the .Release.Name variable is from the root scope and not the current scope
*/}}

{{- define "crm-api-spring-boot-server.ingress.host" -}}
  {{- if .name }}
    {{ .name }}
  {{- else }}
    {{ $.Release.Name }}-{{ $.Release.Namespace }}-{{ .domain }}
  {{- end }}
{{- end}}


{{/*
Common labels
*/}}
{{- define "crm-api-spring-boot-server.labels" -}}
helm.sh/chart: {{ include "crm-api-spring-boot-server.chart" . }}
helm.sh/release-name: {{ .Release.Name }}
helm.sh/release-namespace: {{ .Release.Namespace }}
{{ include "crm-api-spring-boot-server.selectorLabels" . }}
app.kubernetes.io/version: {{ include "crm-api-spring-boot-server.appVersion" . }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "crm-api-spring-boot-server.selectorLabels" -}}
app.kubernetes.io/name: {{ include "crm-api-spring-boot-server.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: application
app.kubernetes.io/part-of: {{ .Values.application.group }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "crm-api-spring-boot-server.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "crm-api-spring-boot-server.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
