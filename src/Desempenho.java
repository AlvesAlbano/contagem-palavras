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

    public static void gerarRelatorioSerial(Runnable runnable, int iteracoes, String nomeLivro) {
        
        final String pasta = String.format("src/relatorios/serial/%s", nomeLivro);
        long somaTempos = 0;

        new File(pasta).mkdirs();

        String nomeArquivo = String.format("%s/%s_serial_relatorio.csv", pasta ,nomeLivro);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("Iteracao,Tempo(ns),Nome_Livro,Paradigma");
            writer.newLine();

            for (int i = 1; i <= iteracoes; i++) {
                long tempo = tempoExecucao(runnable);
                somaTempos += tempo;
                writer.write(String.format("%d,%d,%s,Serial", i, tempo,nomeLivro));
                writer.newLine();
            }

//            double media = calcularMedia(somaTempos,iteracoes);
//
//            writer.newLine();
//            writer.write(String.format("%.3f", media)); // escreve a média no final
//            writer.newLine();

            System.out.printf("arquivo CSV do %s Serial gerado \n",nomeLivro);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gerarRelatorioParalelo(Runnable runnable, int iteracoes, String nomeLivro,int quantidadeThreads) {
        
        final String pasta = String.format("src/relatorios/paralelo/%s", nomeLivro);
        long somaTempos = 0;

        new File(pasta).mkdirs();

        String nomeArquivo = String.format("%s/%s_paralelo_relatorio.csv", pasta ,nomeLivro);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("Iteracao,Tempo(ns),Nome_Livro,Paradigma,Quantidade_Threads");
            writer.newLine();

            for (int i = 1; i <= iteracoes; i++) {
                long tempo = tempoExecucao(runnable);
                somaTempos += tempo;
                writer.write(String.format("%d,%d,%s,Paralelo,%d", i, tempo,nomeLivro,quantidadeThreads));
                writer.newLine();
            }

//            double media = calcularMedia(somaTempos,iteracoes);
//
//            writer.newLine();
//            writer.write(String.format("%.3f", media)); // escreve a média no final
//            writer.newLine();

            System.out.printf("arquivo CSV do %s Paralelo gerado \n",nomeLivro);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gerarRelatorioParaleloGPU(Runnable runnable, int iteracoes, String nomeLivro) {
        
        final String pasta = String.format("src/relatorios/paralelo-GPU/%s", nomeLivro);
        long somaTempos = 0;

        new File(pasta).mkdirs();

        String nomeArquivo = String.format("%s/%s_paraleloGPU_relatorio.csv", pasta ,nomeLivro);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("Iteracao,Tempo(ns),Nome_Livro,Paradigma");
            writer.newLine();

            for (int i = 1; i <= iteracoes; i++) {
                long tempo = tempoExecucao(runnable);
                somaTempos += tempo;
                writer.write(String.format("%d,%d,%s,Paralelo-GPU", i, tempo,nomeLivro));
                writer.newLine();
            }

//            double media = calcularMedia(somaTempos,iteracoes);
//
//            writer.newLine();
//            writer.write(String.format("%.3f", media)); // escreve a média no final
//            writer.newLine();

            System.out.printf("arquivo CSV do %s Paralelo GPU gerado \n",nomeLivro);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double calcularMedia(long somaTempos, int iteacoes){
        return (double) somaTempos / iteacoes;
    }

}