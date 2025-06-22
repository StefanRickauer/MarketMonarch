!define APP_NAME "MarketMonarch"
!define EXE_NAME "MarketMonarch.exe"
!define OUTPUT_NAME "MarketMonarch-Installer.exe"

OutFile "..\..\..\target\${OUTPUT_NAME}"
InstallDir "$PROGRAMFILES\${APP_NAME}"

Page directory
Page instfiles

Section "Install"
  SetOutPath "$INSTDIR"
  File "..\..\..\target\${EXE_NAME}"
  CreateShortCut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${EXE_NAME}"
SectionEnd