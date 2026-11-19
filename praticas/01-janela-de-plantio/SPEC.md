# SPEC: Janela de Plantio (MVP)

Versão 0.1, 19/11/2026. Documento vivo: alterações relevantes entram no [DIARIO.md](../../DIARIO.md).

## 1. Abordagem

- **Problema:** consultar rapidamente se uma data de plantio está numa janela de baixo risco climático para uma cultura e município.
- **Objetivo de aprendizado:** construir um app Android nativo funcional que consome uma API real, com Git organizado desde o primeiro commit.
- **Princípio:** o menor app que mostre dados reais respondendo a dados informados pelo usuário.

## 2. Escopo do MVP

Dentro:

- 1 tela principal com formulário (cultura, município, data pretendida) e resultado.
- Chamada à API Agritec (AgroAPI/Embrapa) para época de plantio e, se couber, cultivares indicadas.
- Estados de tela: carregando, sucesso, erro e sem dados.

Fora (por enquanto):

- Login, mapas, múltiplas telas, exportação de dados.
- Banco de dados relacional (Room).
- Qualquer integração com sensores ou embarcados.

Opcional, só se sobrar tempo: notas de campo salvas com DataStore.

## 3. Dados

- **Fonte principal:** AgroAPI/Embrapa, API Agritec (época de plantio por decêndio com níveis de risco, cultivares indicadas).
- **Plano B:** Open-Meteo (sem chave), mostrando clima atual e previsão para uma coordenada.
- **A confirmar na documentação da API:** parâmetros exatos dos endpoints (por exemplo, se pede código IBGE do município, tipo de solo ou ciclo da cultivar) e limites do plano gratuito.

## 4. Arquitetura

- MVVM: `Screen (Compose)` → `ViewModel (StateFlow)` → `Repository` → `DataSource`.
- `DataSource` é uma interface. Implementações previstas:
  - `AgroApiDataSource` (MVP)
  - `OpenMeteoDataSource` (plano B)
  - `SensorDataSource` (futuro: leituras de sensores via HTTP ou BLE)
- Chaves em `local.properties` e lidas via `BuildConfig`. Nunca no repositório.

## 5. Critérios de aceite

- [ ] O app compila e roda em emulador ou aparelho.
- [ ] O usuário informa cultura, município e data, e vê um resultado vindo da API.
- [ ] Erro de rede ou de chave mostra mensagem clara, sem fechar o app.
- [ ] Nenhuma chave ou segredo no histórico do Git.
- [ ] README com prints e instruções de configuração.
- [ ] APK publicado em Releases.

## 6. Riscos

| Risco | Mitigação |
| --- | --- |
| Cadastro ou limites da AgroAPI atrapalham | Testar a chamada fora do app já na etapa Conhecer; decisão em 26/11; plano B com Open-Meteo |
| Parâmetros da API mais complexos que o esperado | Começar por um único endpoint e uma cultura |
| Erros de Gradle ou dependências | Usar o template padrão do Android Studio e poucas bibliotecas |
| Chave vazar no GitHub | `.gitignore` desde o primeiro commit e revisão antes de cada push |

## 7. Decisões em aberto

- Quais culturas entram no MVP (sugestão: começar com uma só).
- Como o usuário escolhe o município (campo de texto, lista ou código IBGE).
- Se as notas de campo entram ou ficam para depois.
