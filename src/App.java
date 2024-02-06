import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

class Cota implements Comparable<Cota> {
    private String codigo;
    private String nome;

    public Cota(String linha) {
        String[] dados = linha.split(";");
        this.codigo = dados[1];
        this.nome = dados[2];
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public boolean equals(Object obj) {
        return this.codigo.equals(((Curso) obj).getCodigo());
    }

    @Override
    public String toString() {
        return this.nome;
    }

    @Override
    public int compareTo(Cota o) {
        return this.getNome().compareTo(o.getNome());
    }

}

class Candidato implements Comparable<Candidato> {
    private String cpf;
    private String nome;
    private Integer notaRedacao;
    private Integer notaMatematica;
    private Integer notaPortugues;
    private Integer notaBiologicas;
    private Integer notaSociais;

    public Candidato(String linha) {
        String[] dados = linha.split(";");
        this.cpf = dados[1];
        this.nome = dados[2];
        this.notaRedacao = Integer.parseInt(dados[5]);
        this.notaMatematica = Integer.parseInt(dados[6]);
        this.notaPortugues = Integer.parseInt(dados[7]);
        this.notaBiologicas = Integer.parseInt(dados[8]);
        this.notaSociais = Integer.parseInt(dados[9]);
    }

    public int getNotaRedacao() {
        return this.notaRedacao;
    }

    public int getNotaMatematica() {
        return this.notaMatematica;
    }

    public int getNotaPortugues() {
        return this.notaPortugues;
    }

    public int getNotaBiologicas() {
        return this.notaBiologicas;
    }

    public int getNotaSociais() {
        return this.notaSociais;
    }

    public Integer getNotaFinal() {
        return notaRedacao + notaMatematica + notaPortugues + notaBiologicas + notaSociais;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %d", this.cpf, this.nome,
                (this.notaRedacao + this.notaMatematica + this.notaPortugues + this.notaBiologicas + this.notaSociais));
    }

    @Override
    public int compareTo(Candidato o) {
        return Integer.compare(o.getNotaFinal(), this.getNotaFinal());
    }
}

class Curso implements Comparable<Curso> {
    private String codigo;
    private String nome;
    private TreeMap<Cota, Integer> vagas;
    private TreeMap<Cota, List<Candidato>> candidatos;

    public Curso(String linha) {
        String[] dados = linha.split(";");
        this.codigo = dados[1];
        this.nome = dados[2];
        this.vagas = new TreeMap<>();
        this.candidatos = new TreeMap<>();

    }

    public Map<Cota, Integer> getVagas() {
        return this.vagas;
    }

    public Map<Cota, List<Candidato>> getCandidatos() {
        return this.candidatos;
    }

    @Override
    public String toString() {
        // Todo Auto-generated method stub
        return this.nome;
    }

    @Override
    public boolean equals(Object obj) {
        return this.codigo.equals(((Curso) obj).getCodigo());
    }

    public void associarVaga(Cota c, int nrVagas) {
        this.vagas.put(c, nrVagas);
    }

    public void associarCandidato(Cota c, Candidato candidato) {
        if (candidato.getNotaRedacao() < 200 || candidato.getNotaBiologicas() == 0
                || candidato.getNotaMatematica() == 0 || candidato.getNotaPortugues() == 0
                || candidato.getNotaSociais() == 0)
            return;

        if (this.candidatos.get(c) == null) {
            ArrayList<Candidato> listaCandidatos = new ArrayList<>();

            listaCandidatos.add(candidato);
            this.candidatos.put(c, listaCandidatos);

        } else {

            this.candidatos.get(c).add(candidato);

        }
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public int compareTo(Curso o) {
        return this.getNome().compareTo(o.getNome());
    }

}

public class App {
    private ArrayList<Cota> cotas = new ArrayList<>();
    private ArrayList<Curso> cursos = new ArrayList<>();
    private ArrayList<Candidato> estudantes = new ArrayList<>();

    public ArrayList<Cota> getCotas() {
        return this.cotas;
    }

    public ArrayList<Curso> getCursos() {
        return this.cursos;
    }

    public ArrayList<Candidato> getEstudantes() {
        return this.estudantes;
    }

    public static void main(String[] args) throws Exception {
        App app = new App();
        Scanner sc = new Scanner(System.in);

        try (BufferedReader br = new BufferedReader(new FileReader(sc.nextLine()))) {
            sc.close();
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.startsWith("01"))
                    app.getCotas().add(new Cota(linha));
                else if (linha.startsWith("02"))
                    app.getCursos().add(new Curso(linha));
                else if (linha.startsWith("03")) {
                    String[] dados = linha.split(";");
                    Cota cota = app.getCotas().stream().filter(
                            c -> c.getCodigo().equals(dados[2])).findFirst().get();
                    Curso curso = app.getCursos().stream().filter(
                            c -> c.getCodigo().equals(dados[1])).findFirst().get();
                    curso.associarVaga(cota, Integer.parseInt(dados[3]));
                } else if (linha.startsWith("04")) {
                    String[] dados = linha.split(";");
                    Candidato candidato = new Candidato(linha);
                    Cota cota = app.getCotas().stream().filter(
                            c -> c.getCodigo().equals(dados[4])).findFirst().get();
                    Curso curso = app.getCursos().stream().filter(
                            c -> c.getCodigo().equals(dados[3])).findFirst().get();
                    curso.associarCandidato(cota, candidato);

                }

            }

            Collections.sort(app.getCursos());

            app.getCursos().forEach(
                    curso -> {
                        System.out.printf("%s - %d vagas\n", curso,
                                curso.getVagas().values().stream().reduce(Integer::sum).get());
                        curso.getCandidatos().forEach(
                                (cota, estudantes) -> {
                                    System.out.printf("..%s [%d vagas]\n", cota, curso.getVagas().get(cota));
                                    AtomicInteger contador = new AtomicInteger(1);
                                    Collections.sort(estudantes);
                                    int max = curso.getVagas().get(cota) > estudantes.size() ? estudantes.size()
                                            : curso.getVagas().get(cota);
                                    estudantes.subList(0, max).forEach(estudante -> {
                                        System.out.printf("....%d - %s\n", contador.getAndIncrement(),
                                                estudante);
                                    }

                                );
                                }

                    );

                    }

            );

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}