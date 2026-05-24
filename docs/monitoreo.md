
---

## 3. `docs\monitoreo.md`

```markdown
# Monitoreo - FixNear

## Descripción

El monitoreo inicial del proyecto FixNear se realiza mediante GitHub Actions.

Esta herramienta permite observar el estado de cada ejecución del pipeline cada vez que se suben cambios al repositorio.

## Elementos monitoreados

Desde la pestaña Actions de GitHub se pueden monitorear los siguientes elementos:

- Estado del pipeline.
- Tiempo de ejecución.
- Errores de compilación.
- Resultado de pruebas automáticas.
- Historial de ejecuciones.
- Commits asociados a cada ejecución.

## Estados del pipeline

GitHub Actions muestra diferentes estados:

- Verde: ejecución correcta.
- Amarillo: ejecución en proceso.
- Rojo: ejecución fallida.

## Importancia del monitoreo

El monitoreo permite detectar errores de forma temprana durante el desarrollo.

Si un cambio provoca que el proyecto no compile, GitHub Actions lo muestra inmediatamente y permite revisar el log del error.

## Resultado observado

Durante la implementación del pipeline, se observó que el proyecto FixNear fue compilado correctamente y las pruebas automáticas se ejecutaron sin fallos.

## Evidencias

Como evidencia se utilizarán capturas de pantalla de:

- La pestaña Actions.
- Workflows ejecutados.
- Estado verde del pipeline.
- Logs del proceso de compilación.
- Mensaje BUILD SUCCESSFUL.

## Conclusión

El monitoreo mediante GitHub Actions ayuda a mantener la calidad del proyecto, ya que permite revisar automáticamente si los cambios realizados afectan el funcionamiento general de la aplicación.