!define APP_NAME "MarketMonarch"
!define EXE_NAME "MarketMonarch.exe"
!define OUTPUT_NAME "MarketMonarch-Installer.exe"

OutFile "C:\Users\noNameForM3\eclipse-workspace\MarketMonarchTradingBot\target\MarketMonarch-Installer.exe"
InstallDir "$PROGRAMFILES\${APP_NAME}"

Page directory
Page instfiles

Section "Install"
  SetOutPath "$INSTDIR"
  File "C:\Users\noNameForM3\eclipse-workspace\MarketMonarchTradingBot\target\${EXE_NAME}"
  CreateShortCut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${EXE_NAME}"
SectionEnd