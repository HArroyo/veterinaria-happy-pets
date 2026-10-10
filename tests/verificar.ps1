$ErrorActionPreference = 'Stop'
$raizProyecto = Split-Path $PSScriptRoot -Parent
$salidaPruebas = Join-Path $raizProyecto '.local-build/regresion'
New-Item -ItemType Directory -Path $salidaPruebas -Force | Out-Null
$fuentesPruebas = Get-ChildItem (Join-Path $raizProyecto 'src') -Recurse -Filter '*.java' |
    ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' }
$archivoFuentes = Join-Path $salidaPruebas 'fuentes.txt'
[IO.File]::WriteAllLines($archivoFuentes, $fuentesPruebas, [Text.UTF8Encoding]::new($false))
& javac -encoding UTF-8 -d $salidaPruebas "@$archivoFuentes" (Join-Path $PSScriptRoot 'RegresionFuncional.java')
if ($LASTEXITCODE -ne 0) { throw 'La compilación falló.' }
& java '-Djava.awt.headless=true' -ea --module-path $salidaPruebas -m 'VeterinariaHappyPets/happypets.RegresionFuncional'
if ($LASTEXITCODE -ne 0) { throw 'La regresión funcional falló.' }
