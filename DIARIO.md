# Diário de desenvolvimento

Uma entrada curta por sessão de trabalho, da mais recente para a mais antiga. Cada item indica a prática entre colchetes.

## 2026-11-23

- [01-janela-de-plantio] Configurei o ambiente Android no Ubuntu (KVM ativo e Android Studio Rabbit 2) e criei o projeto `JanelaDePlantio` em Kotlin + Jetpack Compose (`praticas/01-janela-de-plantio/android/`).
- [01-janela-de-plantio] Validei a compilação inicial e a execução do "Hello Android!" no emulador (com tratamento de exceções).
- Próximo passo: criar a conta na Loja de APIs da Embrapa, preencher o `.env` e validar o `scripts/testar_agroapi.py`.
  
## 2026-11-19

- [web_mobile-app] Preparei a estrutura do repositório: índice de práticas, modelo de prática e este diário.
- [01-janela-de-plantio] Defini o escopo: app simples, sem embarcados por enquanto, consumindo dados da AgroAPI/Embrapa.
- [01-janela-de-plantio] Preparei SPEC, ROADMAP e um script de teste de acesso à API.
- Próximo passo: criar a conta na Loja de APIs, registrar a aplicação e testar a chamada fora do app.
- Ideia de longo prazo: unir Android e embarcados em etapas, começando pela interface `DataSource`.
