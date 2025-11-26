import pandas as pd
from pathlib import Path
import matplotlib.pyplot as plt

def gerar_grafico(pasta):
    df_total = pd.concat([pd.read_csv(arquivo) for arquivo in pasta.rglob("*.csv")], ignore_index=True)
    # Iteracao,Tempo(ns),Nome,Paradigma,Quantidade_Threads
    nomes = df_total["Nome"].unique()
    paradigmas = df_total["Paradigma"].unique()
    volumes = sorted(df_total["Volume"].unique())

    cores = plt.cm.tab10.colors
    estilos = ['-', '--', '-.', ':']

    for volume in volumes:
        
        df_volume = df_total[df_total["Volume"] == volume]
        
        print(f"volume: {volume}")
        plt.figure(figsize=(12,7))
        plt.title(f"Desempenho dos Algoritmos - Volume de Dados: {volume}")
        plt.xlabel("Iteração")
        plt.ylabel("Tempo (ns)")
        plt.grid(True)

        for i, nome in enumerate(nomes):
            for j, paradigma in enumerate(paradigmas):
                df_filtrado = df_volume[(df_volume["Nome"] == nome) & (df_volume["Paradigma"] == paradigma)]
                if not df_filtrado.empty:
                    # Linha das iterações
                    plt.plot(
                        df_filtrado["Iteracao"],
                        df_filtrado["Tempo(ns)"],
                        color=cores[i % len(cores)],
                        linestyle=estilos[j % len(estilos)],
                        marker='o',
                        label=f"{nome} - {paradigma}"
                    )

                    # Média
                    media = df_filtrado["Tempo(ns)"].mean()
                    print(f"media do {nome}: {media} ({paradigma})")
                    plt.scatter(
                        df_filtrado["Iteracao"].mean(),
                        media,
                        color=cores[i % len(cores)],
                        s=100,
                        edgecolors='black',
                        zorder=5,
                        label=f"Média {nome} - {paradigma}"
                    )

        plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
        plt.tight_layout()
        plt.show()

pasta_serial = Path("src/relatorios/serial")
pasta_paralelo = Path("src/relatorios/paralelo")

gerar_grafico(pasta_serial)
gerar_grafico(pasta_paralelo)