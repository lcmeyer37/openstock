/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierclasses;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author lucasmeyer
 */
public class mcchartgenerator
{

    public org.jfree.chart.JFreeChart chartatual; //chart atual
    public org.jfree.chart.ChartPanel chartpanelatual; //chartpanel atual
    public java.util.List<mierclasses.mccandle> candlesatual; //lista de candles atual utilizadas por este mcg
    public java.util.List<String> idindicadoresatual; //lista com ids dos indicadores atuais presentes no chart (nao serve para indicadores que nao estao desenhados no chart)
    public java.util.List<String> idferramentassubannotationsatual; //lista com ids das ferramentas atuais presentes no chart

    //informacoes sobre a ultima posicao do mouse no chart
    public double valormouseatualgraficox; //contem o ultimo valor em x que o mouse tocou
    public double valormouseatualgraficoy; //contem o ultimo valor em y que o mouse tocou
    public org.jfree.chart.entity.ChartEntity entityatualgrafico; //contem a ultima entidade que o mouse tocou

    //ultima anotacao (lista de subanotacoes pertencentes a esta anotacao)
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> ultimalistasubanotacoesanotacao;

    //dataset utilizado para indicadores no grafico de ohlc
    org.jfree.data.time.TimeSeriesCollection datasetindicadoresohlc;

    //classe que se comunica com jfreecharts para criar graficos de interesse para o programa
    public mcchartgenerator()
    {
        inicializarmcchartgenerator();
    }

    void inicializarmcchartgenerator()
    {
        idindicadoresatual = new java.util.ArrayList<>();
        idferramentassubannotationsatual = new java.util.ArrayList<>();
    }

    // <editor-fold defaultstate="collapsed" desc="Funcoes para funcionamento de Ferramentas no chart OHLC">
    //as ferramentas sao desenhos graficos que podem ser adicionados no grafico. 
    //esses desenhos sao criados com uma ou mais Annotations - exemplo: Line = 1 lineannotation, Fibonacci = 6 linesannotations + 6 textsannotations
    // <editor-fold defaultstate="collapsed" desc="Troca de Ferramentas">
    //ferramentas criam elementos de annotation no grafico ohlc
    //variavel que diz qual a ferramenta atual de edicao do grafico
    public String ferramentaatualgrafico = "selection";

    public void trocarferramentaparaselecao()
    {
        ferramentaatualgrafico = "selection";
    }

    public void trocarferramentapararegua()
    {
        ferramentaatualgrafico = "ruler";
    }

    public void trocarferramentaparareta()
    {
        ferramentaatualgrafico = "line";
    }

    public void trocarferramentaparafibonacci()
    {
        ferramentaatualgrafico = "fibonacci";
    }

    public void trocarferramentaparatexto()
    {
        ferramentaatualgrafico = "text";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Interpretadores de Eventos de Mouse dado Ferramenta Em Uso">
    void interpretarferramenta_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        //sempre atualizar informacoes sobre a posicao atual do mouse no grafico
        //funcao para atualizar o ultimo ponto x e y e a entity selecionada pelo mouse
        java.awt.geom.Point2D p = chartpanelatual.translateScreenToJava2D(cmevent.getTrigger().getPoint());
        java.awt.geom.Rectangle2D plotArea = chartpanelatual.getChartRenderingInfo().getPlotInfo().getDataArea();
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getPlot(); // your plot
        valormouseatualgraficox = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
        valormouseatualgraficoy = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
        entityatualgrafico = cmevent.getEntity();

        //funcao para interpretar a ferramenta atual e manipular o grafico
        if (ferramentaatualgrafico.equals("selection"))
        {
            //no modo de selecao nao eh necessario fazer nada, soh resetar quaisquer ferramentas em uso
            ferramentaselecao_mclick();
        } 
        else if (ferramentaatualgrafico.equals("ruler"))
        {
            ferramentaregua_mclick(cmevent);
        } 
        else if (ferramentaatualgrafico.equals("line"))
        {
            //no modo de reta o usuario clica a primeira vez para dar o primeiro ponto da reta, e uma segunda vez para dar o segundo ponto
            ferramentareta_mclick(cmevent);
        } 
        else if (ferramentaatualgrafico.equals("fibonacci"))
        {
            ferramentafib_mclick(cmevent);
        } 
        else if (ferramentaatualgrafico.equals("text"))
        {
            ferramentatexto_mclick(cmevent);
        }
    }

    void interpretarferramenta_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {
        //sempre atualizar informacoes sobre a posicao atual do mouse no grafico
        //funcao para atualizar o ultimo ponto x e y e a entity selecionada pelo mouse
        java.awt.geom.Point2D p = chartpanelatual.translateScreenToJava2D(cmevent.getTrigger().getPoint());
        java.awt.geom.Rectangle2D plotArea = chartpanelatual.getChartRenderingInfo().getPlotInfo().getDataArea();
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getPlot(); // your plot
        valormouseatualgraficox = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
        valormouseatualgraficoy = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
        entityatualgrafico = cmevent.getEntity();

        //funcao para interpretar a ferramenta atual e manipular o grafico
        if (ferramentaatualgrafico.equals("selection"))
        {
            //no modo de selecao nao eh necessario fazer nada, soh resetar quaisquer ferramentas em uso
            ferramentaselecao_mmove();
        } else if (ferramentaatualgrafico.equals("ruler"))
        {
            ferramentaregua_mmove(cmevent);
        } else if (ferramentaatualgrafico.equals("line"))
        {
            //no modo de reta o usuario clica a primeira vez para dar o primeiro ponto da reta, e uma segunda vez para dar o segundo ponto
            ferramentareta_mmove(cmevent);
        } else if (ferramentaatualgrafico.equals("fibonacci"))
        {
            ferramentafib_mmove(cmevent);
        } else if (ferramentaatualgrafico.equals("text"))
        {
            ferramentatexto_mmove(cmevent);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Selecao">
    void ferramentaselecao_mclick()
    {
    }

    void ferramentaselecao_mmove()
    {
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Texto">
    void ferramentatexto_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        double reta_pontox = valormouseatualgraficox;
        double reta_pontoy = valormouseatualgraficoy;
        ultimalistasubanotacoesanotacao = adicionarplotohlc_annotationtexto(reta_pontox, reta_pontoy);
    }

    void ferramentatexto_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {
        //nada a fazer
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Reta">
    boolean reta_jatemponto1 = false;
    double reta_pontox1 = 0;
    double reta_pontoy1 = 0;
    java.util.List<org.jfree.chart.annotations.XYAnnotation> reta_preview; //reta que fica sendo redesenhada para preview e no fim eh salva com segundo clique

    void ferramentareta_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        if (reta_jatemponto1 == false)
        {
            reta_pontox1 = valormouseatualgraficox;
            reta_pontoy1 = valormouseatualgraficoy;
            reta_jatemponto1 = true;
        } else if (reta_jatemponto1 == true)
        {
            ultimalistasubanotacoesanotacao = reta_preview;
            reta_jatemponto1 = false;
            reta_preview = null;
            reta_pontox1 = 0;
            reta_pontoy1 = 0;
        }
    }

    void ferramentareta_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {

        //no caso de reta move, nos queremos que a annotation apareca constantemente como preview ateh o usuario dar o segundo clique
        if (reta_jatemponto1 == false)
        {
            //caso nao tenha o primeiro ponto, nao desenhar preview ainda 
        } else if (reta_jatemponto1 == true)
        {
            //remover annotation
            try
            {
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartatual.getPlot();

                for (int i = 0; i < reta_preview.size(); i++)
                {
                    plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation) reta_preview.get(i));
                }
            } catch (Exception ex)
            {
            }

            //ja tendo o o primeiro ponto, pode desenhar o preview
            double reta_pontox2 = valormouseatualgraficox;
            double reta_pontoy2 = valormouseatualgraficoy;

            //adicionar annotation line preview nova
            reta_preview = adicionarplotohlc_annotationreta(reta_pontox1, reta_pontoy1, reta_pontox2, reta_pontoy2);

        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Regua">
    boolean regua_jatemponto1 = false;
    double regua_pontox1 = 0;
    double regua_pontoy1 = 0;
    java.util.List<org.jfree.chart.annotations.XYAnnotation> regua_preview;

    void ferramentaregua_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        if (regua_jatemponto1 == false)
        {
            regua_pontox1 = valormouseatualgraficox;
            regua_pontoy1 = valormouseatualgraficoy;
            regua_jatemponto1 = true;
        } else if (regua_jatemponto1 == true)
        {
            regua_jatemponto1 = false;

            //a regua eh temporaria, soh aparece enquanto o mouse esta movendo
            try
            {
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartatual.getPlot();

                for (int i = 0; i < regua_preview.size(); i++)
                {
                    plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation) regua_preview.get(i));
                }
            } catch (Exception ex)
            {
            }

            regua_preview = null;
            regua_pontox1 = 0;
            regua_pontoy1 = 0;
        }
    }

    void ferramentaregua_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {

        if (regua_jatemponto1 == false)
        {
        } else if (regua_jatemponto1 == true)
        {
            //remover annotation
            try
            {
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartatual.getPlot();

                for (int i = 0; i < regua_preview.size(); i++)
                {
                    plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation) regua_preview.get(i));
                }
            } catch (Exception ex)
            {
            }

            //ja tendo o o primeiro ponto, pode desenhar o preview
            double regua_pontox2 = valormouseatualgraficox;
            double regua_pontoy2 = valormouseatualgraficoy;

            //adicionar annotation line preview nova
            regua_preview = adicionarplotohlc_annotationregua(regua_pontox1, regua_pontoy1, regua_pontox2, regua_pontoy2);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Fibonacci">
    boolean fib_jatemponto1 = false;
    double fib_pontox1 = 0;
    double fib_pontoy1 = 0;
    java.util.List<org.jfree.chart.annotations.XYAnnotation> fib_preview; //o retracement de fibonacci se refere a um conjunto de linhas que sera desenhado

    void ferramentafib_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        if (fib_jatemponto1 == false)
        {
            fib_pontox1 = valormouseatualgraficox;
            fib_pontoy1 = valormouseatualgraficoy;
            fib_jatemponto1 = true;
        } else if (fib_jatemponto1 == true)
        {
            ultimalistasubanotacoesanotacao = fib_preview;
            fib_jatemponto1 = false;
            fib_preview = null;
            fib_pontox1 = 0;
            fib_pontoy1 = 0;
        }
    }

    void ferramentafib_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {

        if (fib_jatemponto1 == false)
        {
        } else if (fib_jatemponto1 == true)
        {
            //remover annotation
            try
            {
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartatual.getPlot();

                for (int i = 0; i < fib_preview.size(); i++)
                {
                    plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation) fib_preview.get(i));
                }
            } catch (Exception ex)
            {
            }

            //ja tendo o o primeiro ponto, pode desenhar o preview
            double fib_pontox2 = valormouseatualgraficox;
            double fib_pontoy2 = valormouseatualgraficoy;

            //adicionar annotation line preview nova
            fib_preview = adicionarplotohlc_annotationfib(fib_pontox1, fib_pontoy1, fib_pontox2, fib_pontoy2);

        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funcoes que adicionam Subannotations no Grafico dado Ferramenta Em Uso">
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_annotationtexto(double x, double y)
    {
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();
        org.jfree.chart.annotations.XYTextAnnotation tatexto = new org.jfree.chart.annotations.XYTextAnnotation("New Annotation", x, y);
        tatexto.setTextAnchor(TextAnchor.CENTER_LEFT);
        plot.addAnnotation(tatexto);

        java.util.List<org.jfree.chart.annotations.XYAnnotation> subannotations = new java.util.ArrayList<>();
        subannotations.add(tatexto);

        return subannotations;
    }

    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_annotationreta(double x1, double y1, double x2, double y2)
    {
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();
        org.jfree.chart.annotations.XYLineAnnotation xylineannotation = new org.jfree.chart.annotations.XYLineAnnotation(x1, y1, x2, y2, new BasicStroke(1.0f), Color.red);

        plot.addAnnotation(xylineannotation);

        java.util.List<org.jfree.chart.annotations.XYAnnotation> subannotations = new java.util.ArrayList<>();
        subannotations.add(xylineannotation);

        return subannotations;
    }

    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_annotationfib(double x1, double y1, double x2, double y2)
    {
        //funcao para desenhar fibonacci retracement no grafico

        //fibonacci retracement: https://www.investopedia.com/terms/f/fibonacciretracement.asp
        //deve ser desenhado linhas paralelas seguindo a sequencia de fibonacci
        //o usuario comeca com o ponto (x1,y1) que se refere a altura 0% do retracement e "x" de largura
        //as outras porcentagens cujas linhas devem ser desenhadas sao 23.6%, 38.2%, 50.0%, 61.8% e 100%
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();

        //linha paralela ao eixo x da altura 0%
        double x1_0 = x1;
        double x2_0 = x2;
        double y1_0 = (((y2 - y1) * 0.000) + y1);
        double y2_0 = y1_0;
        org.jfree.chart.annotations.XYLineAnnotation xylalinha0 = new org.jfree.chart.annotations.XYLineAnnotation(x1_0, y1_0, x2_0, y2_0, new BasicStroke(1.0f), Color.black);
        org.jfree.chart.annotations.XYTextAnnotation tatexto0 = new org.jfree.chart.annotations.XYTextAnnotation("0%", x1, y1_0);
        tatexto0.setTextAnchor(TextAnchor.CENTER_RIGHT);

        //linha paralela ao eixo x da altura 23.6% 
        double x1_236 = x1;
        double x2_236 = x2;
        double y1_236 = (((y2 - y1) * 0.236) + y1);
        double y2_236 = y1_236;
        org.jfree.chart.annotations.XYLineAnnotation xylalinha236 = new org.jfree.chart.annotations.XYLineAnnotation(x1_236, y1_236, x2_236, y2_236, new BasicStroke(1.0f), Color.gray);
        org.jfree.chart.annotations.XYTextAnnotation tatexto236 = new org.jfree.chart.annotations.XYTextAnnotation("23.6%", x1, y1_236);
        tatexto236.setTextAnchor(TextAnchor.CENTER_RIGHT);

        //linha paralela ao eixo x da altura 38.2%
        double x1_382 = x1;
        double x2_382 = x2;
        double y1_382 = (((y2 - y1) * 0.382) + y1);
        double y2_382 = y1_382;
        org.jfree.chart.annotations.XYLineAnnotation xylalinha382 = new org.jfree.chart.annotations.XYLineAnnotation(x1_382, y1_382, x2_382, y2_382, new BasicStroke(1.0f), Color.gray);
        org.jfree.chart.annotations.XYTextAnnotation tatexto382 = new org.jfree.chart.annotations.XYTextAnnotation("38.2%", x1, y1_382);
        tatexto382.setTextAnchor(TextAnchor.CENTER_RIGHT);

        //linha paralela ao eixo x da altura 50.0%
        double x1_500 = x1;
        double x2_500 = x2;
        double y1_500 = (((y2 - y1) * 0.500) + y1);
        double y2_500 = y1_500;
        org.jfree.chart.annotations.XYLineAnnotation xylalinha500 = new org.jfree.chart.annotations.XYLineAnnotation(x1_500, y1_500, x2_500, y2_500, new BasicStroke(1.0f), Color.black);
        org.jfree.chart.annotations.XYTextAnnotation tatexto500 = new org.jfree.chart.annotations.XYTextAnnotation("50.0%", x1, y1_500);
        tatexto500.setTextAnchor(TextAnchor.CENTER_RIGHT);

        //linha paralela ao eixo x da altura 61.8%
        double x1_618 = x1;
        double x2_618 = x2;
        double y1_618 = (((y2 - y1) * 0.618) + y1);
        double y2_618 = y1_618;
        org.jfree.chart.annotations.XYLineAnnotation xylalinha618 = new org.jfree.chart.annotations.XYLineAnnotation(x1_618, y1_618, x2_618, y2_618, new BasicStroke(1.0f), Color.gray);
        org.jfree.chart.annotations.XYTextAnnotation tatexto618 = new org.jfree.chart.annotations.XYTextAnnotation("61.8%", x1, y1_618);
        tatexto618.setTextAnchor(TextAnchor.CENTER_RIGHT);

        //linha paralela ao eixo x, e esta altura se refere a maxima atribuida pelo usuario, ou seja a altura y2
        double x1_10000 = x1;
        double x2_10000 = x2;
        double y1_10000 = (((y2 - y1) * 1.000) + y1);
        double y2_10000 = y1_10000;
        org.jfree.chart.annotations.XYLineAnnotation xylalinha10000 = new org.jfree.chart.annotations.XYLineAnnotation(x1_10000, y1_10000, x2_10000, y2_10000, new BasicStroke(1.0f), Color.black);
        org.jfree.chart.annotations.XYTextAnnotation tatexto10000 = new org.jfree.chart.annotations.XYTextAnnotation("100%", x1, y1_10000);
        tatexto10000.setTextAnchor(TextAnchor.CENTER_RIGHT);

        //adicionar linhas do fib retracement
        plot.addAnnotation(xylalinha0);
        plot.addAnnotation(xylalinha236);
        plot.addAnnotation(xylalinha382);
        plot.addAnnotation(xylalinha500);
        plot.addAnnotation(xylalinha618);
        plot.addAnnotation(xylalinha10000);
        plot.addAnnotation(tatexto0);
        plot.addAnnotation(tatexto236);
        plot.addAnnotation(tatexto382);
        plot.addAnnotation(tatexto500);
        plot.addAnnotation(tatexto618);
        plot.addAnnotation(tatexto10000);

        java.util.List<org.jfree.chart.annotations.XYAnnotation> subannotations = new java.util.ArrayList<>();
        subannotations.add(xylalinha0);
        subannotations.add(xylalinha236);
        subannotations.add(xylalinha382);
        subannotations.add(xylalinha500);
        subannotations.add(xylalinha618);
        subannotations.add(xylalinha10000);
        subannotations.add(tatexto0);
        subannotations.add(tatexto236);
        subannotations.add(tatexto382);
        subannotations.add(tatexto500);
        subannotations.add(tatexto618);
        subannotations.add(tatexto10000);

        return subannotations;
    }

    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_annotationregua(double x1, double y1, double x2, double y2)
    {
        //funcao para desenhar uma linha temporaria mostrando a diferenca de porcentagem e tempo entre dois niveis
        // A 
        //  \    
        //   \  
        //    \ 
        //     B
        //  textofinal

        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();

        //linha diagonal
        double x1_diagonal = x1;
        double x2_diagonal = x2;
        double y1_diagonal = y1;
        double y2_diagonal = y2;
        org.jfree.chart.annotations.XYLineAnnotation xyladiagonal = new org.jfree.chart.annotations.XYLineAnnotation(x1_diagonal, y1_diagonal, x2_diagonal, y2_diagonal, new BasicStroke(1.0f), Color.blue);

        //texto datas
        String datacomeco = new java.util.Date((long) x1).toString();
        String datafim = new java.util.Date((long) x2).toString();
        String textodata = datacomeco + " - " + datafim;
        org.jfree.chart.annotations.XYTextAnnotation tatextodata = new org.jfree.chart.annotations.XYTextAnnotation(textodata, x1, y1);
        tatextodata.setTextAnchor(TextAnchor.CENTER_LEFT);

        //texto porcentagem
        String textoporcentagem = String.format("%.2f", (y2 - y1)) + " (" + String.format("%.2f", ((y2 - y1) / y1) * 100) + "%)";
        org.jfree.chart.annotations.XYTextAnnotation tatextoporcentagem = new org.jfree.chart.annotations.XYTextAnnotation(textoporcentagem, x2, y2);
        tatextoporcentagem.setTextAnchor(TextAnchor.CENTER_LEFT);

        //adicionar linhas e textos
        plot.addAnnotation(xyladiagonal);

        plot.addAnnotation(tatextodata);
        plot.addAnnotation(tatextoporcentagem);

        java.util.List<org.jfree.chart.annotations.XYAnnotation> subannotations = new java.util.ArrayList<>();
        subannotations.add(xyladiagonal);

        subannotations.add(tatextodata);
        subannotations.add(tatextoporcentagem);

        return subannotations;
    }

    // </editor-fold>    
    //</editor-fold>
    
    //funcao para adicionar ids referentes a subannotations de uma ferramenta na lista de controle
    public void adicionarplotohlc_ferramentaid(String idadicionar, int numerosubannotations)
    {
        //esta funcao eh necessaria para adicionar o novo id a lista de anotacoes atuais
        //essa adicao deve ser feita manualmente pela logica do core de ferramentas, que funcionam com o mouse

        for (int i = 0; i < numerosubannotations; i++)
        {
            idferramentassubannotationsatual.add(idadicionar);
        }
    }

    //funcao para remover id da lista de ids de ferramentas
    public void removerplotohlc_ferramentaid(String idremover, int numerosubannotations)
    {
        for (int i = 0; i < numerosubannotations; i++)
        {
            idferramentassubannotationsatual.remove(idremover);
        }
    }

    //funcao para adicionar subannotations de uma ferramenta (utilizado ao realizar load de um asset)
    public void adicionarplotohlc_subannotationsobjectbase64type(java.util.List<org.jfree.chart.annotations.XYAnnotation> subannotationsanotacao)
    {
        //funcao especial para carregamento de objetos de annotation
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();

        java.util.List<org.jfree.chart.annotations.XYAnnotation> anotacoesadicionar = (java.util.List<org.jfree.chart.annotations.XYAnnotation>) subannotationsanotacao;
        org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartatual.getPlot();
        for (int i = 0; i < anotacoesadicionar.size(); i++)
        {
            plot.addAnnotation(anotacoesadicionar.get(i));
        }
    }

    //funcao para remover subannotations do grafico ohlc
    public void removerplotohlc_subannotations(java.util.List<org.jfree.chart.annotations.XYAnnotation> subanotacoesanotacao)
    {
        //remover as subanotacoes graficas do chart
        org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartatual.getPlot();
        for (int i = 0; i < subanotacoesanotacao.size(); i++)
        {
            plotatual.removeAnnotation(subanotacoesanotacao.get(i));
            //mierclasses.mcfuncoeshelper.mostrarmensagem("anotacao grafica removida");
        }
    }
    
    public void removerplotohlc_todossubannotations()
    {
        org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartatual.getPlot();
        java.util.List<org.jfree.chart.plot.XYPlot> todassubannotations = plotatual.getAnnotations();
        for (int i = 0; i < todassubannotations.size(); i++)
        {
            plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation)todassubannotations.get(i));
        }
    }

    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes para funcionamento de Indicadores">
    public void adicionarplotohlc_indicador(Object xvalues, Object yvalues, String tituloscript)
    {
        //funcao para adicionar um grafico de indicador sobre o grafico ohlc
        //interpretar valores de x como double, e de y como date
        double[] yvalues_double = (double[]) yvalues;
        java.util.Date[] xvalues_date = (java.util.Date[]) xvalues;

        //criar timeseries com os dados
        org.jfree.data.time.TimeSeries seriesadd = new org.jfree.data.time.TimeSeries(tituloscript);

        for (int i = 0; i < yvalues_double.length; i++)
        {

            org.jfree.data.time.Millisecond millisegundoatual
                    = new org.jfree.data.time.Millisecond(xvalues_date[i]);

            double valoratual = yvalues_double[i];

            seriesadd.add(millisegundoatual, valoratual);
        }
        //adicionar series no dataset
        datasetindicadoresohlc.addSeries(seriesadd);

    }

    public void adicionarplotohlc_indicadorid(String idindicador)
    {
        idindicadoresatual.add(idindicador);
    }

    public void removerplotohlc_indicador(String idindicador)
    {
        //encontra o dataset com o id do indicador para remove-lo do grafico
        for (int i = 0; i < idindicadoresatual.size(); i++)
        {
            String idindicadoratual = (String) idindicadoresatual.get(i);

            if (idindicador.equals(idindicadoratual))
            {
                datasetindicadoresohlc.removeSeries(i);
            }
        }
    }
    
    public void removerplotohlc_indicadorid(String idindicador)
    {
        //remove o idindicador da lista de controle
        idindicadoresatual.remove(idindicador);
    }

    public void removerplotohlc_indicadores()
    {
        //remover todos os indicadores do grafico
        datasetindicadoresohlc.removeAllSeries();
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes para recriar e retornar chart OHLC com indicadores">
    public void recriarohlc(java.util.List<mierclasses.mccandle> catual, String tituloohlc, String tipoescala)
    {
        //limpar lista de id de indicadores considerando que todos os indicadores
        //e anotacoes serao deletados e um novo OLHC chart sera criado
        idindicadoresatual = new java.util.ArrayList<>();
        idferramentassubannotationsatual = new java.util.ArrayList<>();

        // <editor-fold defaultstate="collapsed" desc="Recriar Grafico OHLC">
        //grafico ohlc
        candlesatual = catual;

        //criar dataset
        org.jfree.data.xy.OHLCDataset olhcdataset = criarohlcdataset(candlesatual, tituloohlc);

        //criar grafico novo
        //adicionar dataset OHLC
        org.jfree.chart.axis.DateAxis domainAxis = new org.jfree.chart.axis.DateAxis("");
        if (tipoescala.equals("linear"))
        {
            org.jfree.chart.axis.NumberAxis rangeAxis = new org.jfree.chart.axis.NumberAxis("");
            org.jfree.chart.renderer.xy.CandlestickRenderer renderer = new org.jfree.chart.renderer.xy.CandlestickRenderer();
            org.jfree.data.xy.XYDataset dataset = (org.jfree.data.xy.XYDataset) olhcdataset;
            org.jfree.chart.plot.XYPlot mainPlot = new org.jfree.chart.plot.XYPlot(dataset, domainAxis, rangeAxis, renderer);
            renderer.setSeriesPaint(0, Color.BLACK);
            renderer.setDrawVolume(true);
            rangeAxis.setAutoRangeIncludesZero(false);
            org.jfree.chart.JFreeChart chart = new org.jfree.chart.JFreeChart(tituloohlc.toUpperCase(), null, mainPlot, false);
            chartatual = chart;
        } 
        else if (tipoescala.equals("logaritmica"))
        {
            org.jfree.chart.axis.LogAxis rangeAxis = new org.jfree.chart.axis.LogAxis("");
            org.jfree.chart.renderer.xy.CandlestickRenderer renderer = new org.jfree.chart.renderer.xy.CandlestickRenderer();
            org.jfree.data.xy.XYDataset dataset = (org.jfree.data.xy.XYDataset) olhcdataset;
            org.jfree.chart.plot.XYPlot mainPlot = new org.jfree.chart.plot.XYPlot(dataset, domainAxis, rangeAxis, renderer);
            renderer.setSeriesPaint(0, Color.BLACK);
            renderer.setDrawVolume(true);
            org.jfree.chart.JFreeChart chart = new org.jfree.chart.JFreeChart(tituloohlc.toUpperCase(), null, mainPlot, false);
            chartatual = chart;
        }
        //adicionar dataset vazio para indicadores
        org.jfree.chart.renderer.xy.XYLineAndShapeRenderer rendereradd = new org.jfree.chart.renderer.xy.DefaultXYItemRenderer();
        rendereradd.setBaseShapesVisible(false);
        rendereradd.setBaseStroke(new BasicStroke(2.0f));
        org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartatual.getPlot();
        datasetindicadoresohlc = new org.jfree.data.time.TimeSeriesCollection();
        plotatual.setDataset(1, datasetindicadoresohlc);
        plotatual.setRenderer(1, rendereradd);

        // Create Panel
        org.jfree.chart.ChartPanel chartpanel = new org.jfree.chart.ChartPanel(chartatual);
        chartpanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener()
        {
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent e)
            {
                //mierfuncoeshelper.mostrarmensagem("OLA");
                interpretarferramenta_mclick(e);
            }

            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent e)
            {
                interpretarferramenta_mmove(e);
            }
        });
        chartpanelatual = chartpanel;
        //</editor-fold>

    }

    public org.jfree.chart.ChartPanel retornarcpanelohlc()
    {
        return chartpanelatual;
    }

    org.jfree.data.xy.OHLCDataset criarohlcdataset(java.util.List<mierclasses.mccandle> candles, String tituloohlc)
    {
        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        //org.jfree.data.xy.OHLCDataItem dataItem[] = null;

        java.util.List<org.jfree.data.xy.OHLCDataItem> listohlcitems = new java.util.ArrayList<>();

        for (int i = 0; i < candles.size(); i++)
        {
            mierclasses.mccandle candleatual = candles.get(i);

            org.jfree.data.xy.OHLCDataItem dataitemadd
                    = new org.jfree.data.xy.OHLCDataItem(
                            candleatual.timestampdate,
                            candleatual.opend,
                            candleatual.highd,
                            candleatual.lowd,
                            candleatual.closed,
                            candleatual.volumed
                    );

            listohlcitems.add(dataitemadd);
        }

        org.jfree.data.xy.OHLCDataItem[] arrayohlcitems = new org.jfree.data.xy.OHLCDataItem[listohlcitems.size()];
        arrayohlcitems = listohlcitems.toArray(arrayohlcitems);

        org.jfree.data.xy.OHLCDataset datasetretornar = new org.jfree.data.xy.DefaultOHLCDataset(tituloohlc, arrayohlcitems);

        return datasetretornar;
    }
    
    public void recarregarohlc(java.util.List<mierclasses.mccandle> catual, String tituloohlc, String tipoescala)
    {
        //sabendo que este grafico ohlc ja foi criado, 
        //caso soh seja necessario recarregar seus dados,
        //esta funcao deve ser rodada

        //grafico ohlc
        candlesatual = catual;

        //recriar dataset com candles
        org.jfree.data.xy.OHLCDataset olhcdataset = criarohlcdataset(candlesatual, tituloohlc);

        //criar grafico novo
        //adicionar dataset OHLC
        org.jfree.chart.axis.DateAxis domainAxis = new org.jfree.chart.axis.DateAxis("");
        if (tipoescala.equals("linear"))
        {
            org.jfree.chart.axis.NumberAxis rangeAxis = new org.jfree.chart.axis.NumberAxis("");
            org.jfree.chart.renderer.xy.CandlestickRenderer renderer = new org.jfree.chart.renderer.xy.CandlestickRenderer();
            org.jfree.data.xy.XYDataset dataset = (org.jfree.data.xy.XYDataset) olhcdataset;
            org.jfree.chart.plot.XYPlot mainPlot = new org.jfree.chart.plot.XYPlot(dataset, domainAxis, rangeAxis, renderer);
            renderer.setSeriesPaint(0, Color.BLACK);
            renderer.setDrawVolume(true);
            rangeAxis.setAutoRangeIncludesZero(false);
            org.jfree.chart.JFreeChart chart = new org.jfree.chart.JFreeChart(tituloohlc.toUpperCase(), null, mainPlot, false);
            chartatual = chart;
        } 
        else if (tipoescala.equals("logaritmica"))
        {
            org.jfree.chart.axis.LogAxis rangeAxis = new org.jfree.chart.axis.LogAxis("");
            org.jfree.chart.renderer.xy.CandlestickRenderer renderer = new org.jfree.chart.renderer.xy.CandlestickRenderer();
            org.jfree.data.xy.XYDataset dataset = (org.jfree.data.xy.XYDataset) olhcdataset;
            org.jfree.chart.plot.XYPlot mainPlot = new org.jfree.chart.plot.XYPlot(dataset, domainAxis, rangeAxis, renderer);
            renderer.setSeriesPaint(0, Color.BLACK);
            renderer.setDrawVolume(true);
            org.jfree.chart.JFreeChart chart = new org.jfree.chart.JFreeChart(tituloohlc.toUpperCase(), null, mainPlot, false);
            chartatual = chart;
        }
        //adicionar dataset vazio para indicadores
        org.jfree.chart.renderer.xy.XYLineAndShapeRenderer rendereradd = new org.jfree.chart.renderer.xy.DefaultXYItemRenderer();
        rendereradd.setBaseShapesVisible(false);
        rendereradd.setBaseStroke(new BasicStroke(2.0f));
        org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartatual.getPlot();
        datasetindicadoresohlc = new org.jfree.data.time.TimeSeriesCollection();
        plotatual.setDataset(1, datasetindicadoresohlc);
        plotatual.setRenderer(1, rendereradd);

        // Create Panel
        org.jfree.chart.ChartPanel chartpanel = new org.jfree.chart.ChartPanel(chartatual);
        chartpanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener()
        {
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent e)
            {
                interpretarferramenta_mclick(e);
            }

            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent e)
            {
                interpretarferramenta_mmove(e);
            }
        });
        chartpanelatual = chartpanel;
        //</editor-fold>

    }


    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes Helper">
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> retornartodassubanotacoes()
    {
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();
        java.util.List<org.jfree.chart.annotations.XYAnnotation> listasubannotations = plot.getAnnotations();
        return listasubannotations;
    }

    //funcao de teste
    public void printlistaidsanotacao()
    {
        String listaids = "";
        for (int i = 0; i < idferramentassubannotationsatual.size(); i++)
        {
            listaids = listaids + idferramentassubannotationsatual.get(i) + "\n";
        }
        //mierclasses.mcfuncoeshelper.mostrarmensagem(listaids);
    }

    public void printlistaidsindicador()
    {
        String listaids = "";
        for (int i = 0; i < idindicadoresatual.size(); i++)
        {
            listaids = listaids + idindicadoresatual.get(i) + "\n";
        }
        //mierclasses.mcfuncoeshelper.mostrarmensagem(listaids);
    }
    //</editor-fold>
}
