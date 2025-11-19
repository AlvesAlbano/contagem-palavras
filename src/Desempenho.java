import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Desempenho {

    public static long tempoExecucao(Runnable runnable){
        long tempoInicio = System.nanoTime();
        runnable.run();
        long tempoFim = System.nanoTime();

        return tempoFim - tempoInicio;
    }

    public static double execucaoMedia(Runnable runnable, int iteracoes){
        
        long total = 0;
        for (int i = 0; i < iteracoes; i++) {
            total += tempoExecucao(runnable);
        }

        return total / iteracoes;
    }

    public static void gerarRelatorioSerial(Runnable runnable, int iteracoes, String nomeAlgoritmo, long volumeDados) {
        
        final String pasta = String.format("src/relatorios/serial/volume_de_%d", volumeDados);
        long somaTempos = 0;

        new File(pasta).mkdirs();

        String nomeArquivo = String.format("%s/%s_%d_relatorio.csv", pasta ,nomeAlgoritmo,volumeDados);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("Iteracao,Tempo(ns),Nome,Paradigma,Volume");
            writer.newLine();

            for (int i = 1; i <= iteracoes; i++) {
                long tempo = tempoExecucao(runnable);
                somaTempos += tempo;
                writer.write(String.format("%d,%d,%s,Serial,%d", i, tempo,nomeAlgoritmo,volumeDados));
                writer.newLine();
            }

            // double media = (double) somaTempos / iteracoes;

            // writer.newLine();
            // writer.write(String.format("%.3f", media)); // escreve a média no final
            // writer.newLine();

            System.out.printf("relatório do %s Serial gerado \n",nomeAlgoritmo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gerarRelatorioParalelo(Runnable runnable, int iteracoes, String nomeAlgoritmo, long volumeDados, int quantidadeThreads) {
        
        final String pasta = String.format("src/relatorios/paralelo/volume_de_%d", volumeDados);
        long somaTempos = 0;
        new File(pasta).mkdirs();

        String nomeArquivo = String.format("%s/%s_%d_%d_relatorio.csv", pasta ,nomeAlgoritmo,volumeDados,quantidadeThreads);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("Iteracao,Tempo(ns),Nome,Paradigma,Volume");
            writer.newLine();

            for (int i = 1; i <= iteracoes; i++) {
                long tempo = tempoExecucao(runnable);
                somaTempos += tempo;
                writer.write(String.format("%d,%d,%s,Paralelo,%d", i, tempo,nomeAlgoritmo,volumeDados));
                writer.newLine();
            }

            // double media = (double) somaTempos / iteracoes;

            // writer.newLine();
            // writer.write(String.format("%.3f", media)); // escreve a média no final
            // writer.newLine();

            System.out.printf("relatório do %s Paralelo gerado \n",nomeAlgoritmo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}