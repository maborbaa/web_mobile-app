#!/usr/bin/env python3
"""Teste de acesso à AgroAPI (Embrapa), sem dependências externas.

Antes de rodar:
  1. Crie uma aplicação na Loja de APIs e assine a API Agritec (plano gratuito).
  2. Copie .env.example para .env (na pasta da prática) e preencha AGROAPI_KEY e
     AGROAPI_SECRET. Variáveis já definidas no terminal têm prioridade sobre o .env.
  3. Rode, a partir da pasta da prática:  python3 scripts/testar_agroapi.py

O script nunca imprime a chave, o segredo nem o token.

Os endereços padrão abaixo vêm de convenções da plataforma e DEVEM ser conferidos
na aba Production Keys e no API Console da Loja de APIs. Se forem diferentes,
defina AGROAPI_TOKEN_URL e AGROAPI_CULTURAS_URL no .env.
"""
import base64
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

TOKEN_URL_PADRAO = "https://api.cnptia.embrapa.br/token"
CULTURAS_URL_PADRAO = "https://api.cnptia.embrapa.br/agritec/v1/culturas"
TIMEOUT_S = 20


def carregar_env() -> None:
    """Lê NOME=VALOR do .env da pasta da prática, sem sobrescrever o ambiente."""
    caminho = Path(__file__).resolve().parent.parent / ".env"
    if not caminho.is_file():
        return
    for linha in caminho.read_text(encoding="utf-8").splitlines():
        linha = linha.strip()
        if not linha or linha.startswith("#") or "=" not in linha:
            continue
        nome, _, valor = linha.partition("=")
        nome = nome.strip()
        valor = valor.strip().strip('"').strip("'")
        if nome and nome not in os.environ:
            os.environ[nome] = valor


def pedir_token(token_url: str, chave: str, segredo: str):
    """Troca Consumer Key/Secret por um Access Token (client credentials)."""
    basico = base64.b64encode(f"{chave}:{segredo}".encode()).decode()
    corpo = urllib.parse.urlencode({"grant_type": "client_credentials"}).encode()
    req = urllib.request.Request(
        token_url,
        data=corpo,
        method="POST",
        headers={
            "Authorization": f"Basic {basico}",
            "Content-Type": "application/x-www-form-urlencoded",
        },
    )
    with urllib.request.urlopen(req, timeout=TIMEOUT_S) as resp:
        dados = json.load(resp)
    token = dados.get("access_token")
    if not token:
        raise RuntimeError("A resposta não trouxe access_token.")
    return token, dados.get("expires_in")


def consultar(url: str, token: str):
    req = urllib.request.Request(
        url,
        headers={"Authorization": f"Bearer {token}", "Accept": "application/json"},
    )
    with urllib.request.urlopen(req, timeout=TIMEOUT_S) as resp:
        return json.load(resp)


def explicar_http(e: urllib.error.HTTPError, etapa: str) -> None:
    trecho = e.read().decode("utf-8", errors="replace")[:300]
    print(f"Falha na etapa '{etapa}': HTTP {e.code} {e.reason}")
    if trecho:
        print(f"Resposta (início): {trecho}")
    if e.code in (401, 403):
        print(
            "Dica: confira a chave/segredo e se você assinou a API (botão Subscribe) "
            "em um plano gratuito na Loja de APIs."
        )
    if e.code == 404:
        print("Dica: confira o endereço no API Console da Loja de APIs.")


def main() -> int:
    carregar_env()
    chave = os.environ.get("AGROAPI_KEY", "").strip()
    segredo = os.environ.get("AGROAPI_SECRET", "").strip()
    token_url = os.environ.get("AGROAPI_TOKEN_URL", TOKEN_URL_PADRAO).strip()
    culturas_url = os.environ.get("AGROAPI_CULTURAS_URL", CULTURAS_URL_PADRAO).strip()

    if not chave or not segredo:
        print("Defina AGROAPI_KEY e AGROAPI_SECRET no .env ou no terminal (veja .env.example).")
        return 2

    try:
        token, expira = pedir_token(token_url, chave, segredo)
        print(f"Token obtido (expira em: {expira if expira is not None else 'não informado'} s).")
    except urllib.error.HTTPError as e:
        explicar_http(e, "token")
        return 1
    except (urllib.error.URLError, TimeoutError, RuntimeError, json.JSONDecodeError) as e:
        print(f"Falha ao obter o token: {e}")
        return 1

    try:
        dados = consultar(culturas_url, token)
    except urllib.error.HTTPError as e:
        explicar_http(e, "consulta")
        return 1
    except (urllib.error.URLError, TimeoutError, json.JSONDecodeError) as e:
        print(f"Falha na consulta: {e}")
        return 1

    itens = dados.get("data", dados) if isinstance(dados, dict) else dados
    if isinstance(itens, list):
        print(f"Consulta OK: {len(itens)} itens. Primeiros:")
        for item in itens[:5]:
            print(" -", json.dumps(item, ensure_ascii=False))
    else:
        print("Consulta OK. Resposta:")
        print(json.dumps(dados, ensure_ascii=False)[:500])
    return 0


if __name__ == "__main__":
    sys.exit(main())
