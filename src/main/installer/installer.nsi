!define APP_NAME "MarketMonarch"
!define EXE_NAME "MarketMonarch.exe"
!define OUTPUT_NAME "MarketMonarch-Installer.exe"

OutFile "..\..\..\target\${OUTPUT_NAME}"
InstallDir "$PROGRAMFILES\${APP_NAME}"

RequestExecutionLevel admin

Page directory
Page instfiles
UninstPage uninstConfirm
UninstPage instfiles

Section "Install"
  SetOutPath "$INSTDIR"

  ; Hauptprogramm
  File "..\..\..\target\${EXE_NAME}"

  ; Backup-Ordner aus Projekt-Root mitliefern
  File /r "..\..\..\backup"

  ; Desktop-Verknüpfung
  CreateShortCut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${EXE_NAME}"

  ; Uninstaller erzeugen
  WriteUninstaller "$INSTDIR\Uninstall.exe"

  ; Registry für "Programme & Features"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayName" "${APP_NAME}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "NoRepair" 1
SectionEnd

Section "Uninstall"
  ; Dateien löschen
  Delete "$INSTDIR\${EXE_NAME}"
  Delete "$INSTDIR\Uninstall.exe"
  Delete "$DESKTOP\${APP_NAME}.lnk"

  ; Vom Programm erzeugte Verzeichnisse löschen
  RMDir /r "$INSTDIR\sessions"
  RMDir /r "$INSTDIR\logs"
  RMDir /r "$INSTDIR\backup"

  ; Installationsordner löschen
  RMDir "$INSTDIR"

  ; Registry-Eintrag entfernen
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}"
SectionEnd
