import pandas as pd
from pathlib import Path
import matplotlib.pyplot as plt


def gerar_grafico_grupo(pasta: Path, nome_saida: str):
    """
    Gera um gráfico único contendo todas as execuções da pasta.
    Cada linha representa um arquivo CSV ou grupo equivalente.
    """

    arquivos_csv = list(pasta.rglob("*.csv"))

    if not arquivos_csv:
        print(f"Nenhum CSV encontrado em {pasta}")
        return

    # Carregar todos os CSVs
    df_total = []
    for arq in arquivos_csv:
        df = pd.read_csv(arq)
        df["Arquivo"] = arq.stem  # Identificação da linha
        df_total.append(df)

    df_total = pd.concat(df_total, ignore_index=True)

    # Detectar se há threads
    tem_threads = "Quantidade_Threads" in df_total.columns

    # Criar pasta de saída
    pasta_graficos = Path("graficos")
    pasta_graficos.mkdir(exist_ok=True)

    plt.figure(figsize=(12, 6))

    # Agrupar corretamente
    if tem_threads:
        grupos = df_total.groupby(["Nome_Livro", "Quantidade_Threads", "Arquivo"])
    else:
        grupos = df_total.groupby(["Nome_Livro", "Arquivo"])

    # Plota cada grupo como uma linha
    # cores opcionais
    cores = plt.cm.tab10.colors
    i = 0

    # Plota cada grupo como uma linha
    for chave, df in grupos:
        df = df.sort_values("Iteracao")

    grupos = df_total.groupby("Nome_Livro")
    
    cores = plt.cm.tab10.colors
    i = 0
    
    for nome_livro, df in grupos:
    
        df = df.sort_values("Iteracao")
        paradigma = df["Paradigma"].iloc[0]
    
        # Cálculo da média
        media_tempo = df["Tempo(ns)"].mean()
        media_iter = df["Iteracao"].mean()  # posição do ponto da média
    
        print(f"Média do {nome_livro}: {media_tempo:.2f} ns ({paradigma})")
    
        # Média como ponto destacado
        # plt.scatter(
        #     media_iter,
        #     media_tempo,
        #     color=cores[i % len(cores)],
        #     s=150,
        #     edgecolors="black",
        #     zorder=5,
        #     label=f"Média {nome_livro}"
        # )
    
        # Linha principal do livro
        plt.plot(
            df["Iteracao"],
            df["Tempo(ns)"],
            label=nome_livro,
            color=cores[i % len(cores)],
            linewidth=2
        )
    
        i += 1


    plt.title(f"Desempenho Dos Algoritmos - {nome_saida}")
    plt.xlabel("Iterações")
    plt.ylabel("Tempo (ns)")
    plt.grid(True)
    plt.legend(fontsize=8)

    caminho = pasta_graficos / f"{nome_saida}.png"
    plt.tight_layout()
    plt.savefig(caminho, dpi=300, bbox_inches="tight")
    plt.close()

    print(f"Gráfico gerado: {caminho}")


# PASTAS
pasta_serial = Path("src/relatorios/serial")
pasta_paralelo = Path("src/relatorios/paralelo")
pasta_paraleloGPU = Path("src/relatorios/paralelo-GPU")

# GERAR 3 GRÁFICOS
gerar_grafico_grupo(pasta_serial, "serial")
gerar_grafico_grupo(pasta_paralelo, "paralelo")
gerar_grafico_grupo(pasta_paraleloGPU, "paralelo-GPU")