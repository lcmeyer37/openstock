/*
 * Copyright (C) 2019 Lucas Meyer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    // <editor-fold defaultstate="collapsed" desc="Variaveis MCG">
    
    //chartpanel do OHLC
    public org.jfree.chart.ChartPanel mcg_chartpanel; 
    //candles do OHLC
    public java.util.List<mierclasses.mccandle> mcg_candles; 
    //lista para controle, com IDs dos indicadores presentes no OHLC
    public java.util.List<String> mcg_indicadoresIDS; 
    //lista para controle, com IDs das anotacoes, presentes no OHLC
    public java.util.List<String> mcg_anotacoesIDS;
    //anotacao em hold, esta variavel contem a ultima anotacao adicionada pelo usuario, que nao esta na lista de IDs
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> anotacao_emhold;
    //valor OHLC do ultimo toque do mouse em x
    public double mcg_posmousex;
    //valor OHLC do ultimo toque do mouse em y
    public double mcg_posmousey;
    //rangex do OHLC
    public org.jfree.data.Range mcg_rangex;
    //rangey do OHLC
    public org.jfree.data.Range mcg_rangey;

    //jfreechart do OHLC
    org.jfree.chart.JFreeChart mcg_chart; 
    //dataset com timeseries dos indicadores do OHLC
    org.jfree.data.time.TimeSeriesCollection timeseriesindicadoresohlc;
    
    // </editor-fold >

    //classe que se comunica com jfreecharts para criar graficos de interesse para o programa
    public mcchartgenerator()
    {
    }

    // <editor-fold defaultstate="collapsed" desc="Funcoes para funcionamento de Anotacoes">
    
    //as anotacoes sao desenhos graficos criados pelos usuarios, compostos por uma lista de xyannotations
    //exemplo: Line = 1 lineannotation = 1 xyannotation, Fibonacci = 6 linesannotations + 6 textsannotations = 12 xyannotations
    //tambem existem anotacoesfixas, que sao inseridas no grafico sem o pedido do usuario
    //exemplos: crosshair, linha close, etc...
    
    // <editor-fold defaultstate="collapsed" desc="Troca de Anotacao">
    public String anotacaoselecionada = "selection";
    public void selecionaranotacao_selection()
    {
        anotacaoselecionada = "selection";
    }

    public void selecionaranotacao_ruler()
    {
        anotacaoselecionada = "ruler";
    }

    public void selecionaranotacao_line()
    {
        anotacaoselecionada = "line";
    }

    public void selecionaranotacao_fibonacci()
    {
        anotacaoselecionada = "fibonacci";
    }

    public void selecionaranotacao_text()
    {
        anotacaoselecionada = "text";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Interpretadores de Eventos de Mouse dado Anotacao Em Uso">
    
    //realizar funcao da anotacao selecionada dado click no OHLC chart
    void interpretaranotacao_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        //sempre atualizar informacoes sobre a posicao atual do mouse no grafico
        //atualizar o ultimo ponto x e y, ranges e a entity selecionada pelo mouse
        java.awt.geom.Point2D p = mcg_chartpanel.translateScreenToJava2D(cmevent.getTrigger().getPoint());
        java.awt.geom.Rectangle2D plotArea = mcg_chartpanel.getChartRenderingInfo().getPlotInfo().getDataArea();
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot(); // your plot
        mcg_posmousex = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
        mcg_posmousey = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
        mcg_rangex = plot.getDomainAxis().getRange();
        mcg_rangey = plot.getRangeAxis().getRange();

        //mierclasses.mcfuncoeshelper.mostrarmensagem("click test");
        
        //funcao para interpretar a ferramenta atual e manipular o grafico
        if (anotacaoselecionada.equals("selection"))
        {
            //no modo de selecao nao eh necessario fazer nada, soh resetar quaisquer ferramentas em uso
            executaranotacao_selecton_mclick();
        } 
        else if (anotacaoselecionada.equals("ruler"))
        {
            executaranotacao_ruler_mclick(cmevent);
        } 
        else if (anotacaoselecionada.equals("line"))
        {
            //no modo de reta o usuario clica a primeira vez para dar o primeiro ponto da reta, e uma segunda vez para dar o segundo ponto
            executaranotacao_line_mclick(cmevent);
        } 
        else if (anotacaoselecionada.equals("fibonacci"))
        {
            executaranotacao_fibonacci_mclick(cmevent);
        } 
        else if (anotacaoselecionada.equals("text"))
        {
            executaranotacao_text_mclick(cmevent);
        }
    }

    //realizar funcao da anotacao selecionada dado move no OHLC chart
    void interpretaranotacao_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {
        //sempre atualizar informacoes sobre a posicao atual do mouse no grafico
        //atualizar o ultimo ponto x e y, ranges e a entity selecionada pelo mouse
        java.awt.geom.Point2D p = mcg_chartpanel.translateScreenToJava2D(cmevent.getTrigger().getPoint());
        java.awt.geom.Rectangle2D plotArea = mcg_chartpanel.getChartRenderingInfo().getPlotInfo().getDataArea();
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot(); // your plot
        mcg_posmousex = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
        mcg_posmousey = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
        mcg_rangex = plot.getDomainAxis().getRange();
        mcg_rangey = plot.getRangeAxis().getRange();
        
        //mierclasses.mcfuncoeshelper.mostrarmensagem("move test");

        //funcao para interpretar a ferramenta atual e manipular o grafico
        if (anotacaoselecionada.equals("selection"))
        {
            //no modo de selecao nao eh necessario fazer nada, soh resetar quaisquer ferramentas em uso
            executaranotacao_selection_mmove();
        } 
        else if (anotacaoselecionada.equals("ruler"))
        {
            executaranotacao_ruler_mmove(cmevent);
        } 
        else if (anotacaoselecionada.equals("line"))
        {
            //no modo de reta o usuario clica a primeira vez para dar o primeiro ponto da reta, e uma segunda vez para dar o segundo ponto
            executaranotacao_line_mmove(cmevent);
        }
        else if (anotacaoselecionada.equals("fibonacci"))
        {
            executaranotacao_fibonacci_mmove(cmevent);
        }
        else if (anotacaoselecionada.equals("text"))
        {
            executaranotacao_text_mmove(cmevent);
        }
        executaranotacaofixa_crosshair_mmove(cmevent);
    }

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Selecao">
    void executaranotacao_selecton_mclick()
    {
    }

    void executaranotacao_selection_mmove()
    {
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Texto">
    void executaranotacao_text_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        double reta_pontox = mcg_posmousex;
        double reta_pontoy = mcg_posmousey;
        anotacao_emhold = adicionarplotohlc_anotacaotext(reta_pontox, reta_pontoy);
    }

    void executaranotacao_text_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {
        //nada a fazer
    }
    
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_anotacaotext(double x, double y)
    {
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) mcg_chart.getXYPlot();
        org.jfree.chart.annotations.XYTextAnnotation tatexto = new org.jfree.chart.annotations.XYTextAnnotation("New Annotation", x, y);
        tatexto.setTextAnchor(TextAnchor.CENTER_LEFT);
        plot.addAnnotation(tatexto);

        java.util.List<org.jfree.chart.annotations.XYAnnotation> subannotations = new java.util.ArrayList<>();
        subannotations.add(tatexto);

        return subannotations;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Reta">
    boolean reta_jatemponto1 = false;
    double reta_pontox1 = 0;
    double reta_pontoy1 = 0;
    java.util.List<org.jfree.chart.annotations.XYAnnotation> reta_preview; //reta que fica sendo redesenhada para preview e no fim eh salva com segundo clique

    void executaranotacao_line_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        if (reta_jatemponto1 == false)
        {
            reta_pontox1 = mcg_posmousex;
            reta_pontoy1 = mcg_posmousey;
            reta_jatemponto1 = true;
        } else if (reta_jatemponto1 == true)
        {
            anotacao_emhold = reta_preview;
            reta_jatemponto1 = false;
            reta_preview = null;
            reta_pontox1 = 0;
            reta_pontoy1 = 0;
        }
    }

    void executaranotacao_line_mmove(org.jfree.chart.ChartMouseEvent cmevent)
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
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot();

                for (int i = 0; i < reta_preview.size(); i++)
                {
                    plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation) reta_preview.get(i));
                }
            } catch (Exception ex)
            {
            }

            //ja tendo o o primeiro ponto, pode desenhar o preview
            double reta_pontox2 = mcg_posmousex;
            double reta_pontoy2 = mcg_posmousey;

            //adicionar annotation line preview nova
            reta_preview = adicionarplotohlc_anotacaoline(reta_pontox1, reta_pontoy1, reta_pontox2, reta_pontoy2);

        }
    }
    
    
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_anotacaoline(double x1, double y1, double x2, double y2)
    {
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) mcg_chart.getXYPlot();
        org.jfree.chart.annotations.XYLineAnnotation xylineannotation = new org.jfree.chart.annotations.XYLineAnnotation(x1, y1, x2, y2, new BasicStroke(1.0f), Color.red);

        plot.addAnnotation(xylineannotation);

        java.util.List<org.jfree.chart.annotations.XYAnnotation> subannotations = new java.util.ArrayList<>();
        subannotations.add(xylineannotation);

        return subannotations;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Regua">
    boolean regua_jatemponto1 = false;
    double regua_pontox1 = 0;
    double regua_pontoy1 = 0;
    java.util.List<org.jfree.chart.annotations.XYAnnotation> regua_preview;

    void executaranotacao_ruler_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        if (regua_jatemponto1 == false)
        {
            regua_pontox1 = mcg_posmousex;
            regua_pontoy1 = mcg_posmousey;
            regua_jatemponto1 = true;
        } else if (regua_jatemponto1 == true)
        {
            regua_jatemponto1 = false;

            //a regua eh temporaria, soh aparece enquanto o mouse esta movendo
            try
            {
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot();

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

    void executaranotacao_ruler_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {

        if (regua_jatemponto1 == false)
        {
        } 
        else if (regua_jatemponto1 == true)
        {
            //remover annotation
            try
            {
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot();

                for (int i = 0; i < regua_preview.size(); i++)
                {
                    plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation) regua_preview.get(i));
                }
            } catch (Exception ex)
            {
            }

            //ja tendo o o primeiro ponto, pode desenhar o preview
            double regua_pontox2 = mcg_posmousex;
            double regua_pontoy2 = mcg_posmousey;

            //adicionar annotation line preview nova
            regua_preview = adicionarplotohlc_anotacaoruler(regua_pontox1, regua_pontoy1, regua_pontox2, regua_pontoy2);
        }
    }
    
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_anotacaoruler(double x1, double y1, double x2, double y2)
    {
        //funcao para desenhar uma linha temporaria mostrando a diferenca de porcentagem e tempo entre dois niveis
        // A 
        //  \    
        //   \  
        //    \ 
        //     B
        //  textofinal

        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) mcg_chart.getXYPlot();

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
        tatextodata.setTextAnchor(TextAnchor.TOP_LEFT);

        //texto porcentagem
        String textoporcentagem = String.format("%.2f", (y2 - y1)) + " (" + String.format("%.2f", ((y2 - y1) / y1) * 100) + "%)";
        org.jfree.chart.annotations.XYTextAnnotation tatextoporcentagem = new org.jfree.chart.annotations.XYTextAnnotation(textoporcentagem, x2, y2);
        tatextoporcentagem.setTextAnchor(TextAnchor.TOP_LEFT);

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

    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Fibonacci">
    boolean fib_jatemponto1 = false;
    double fib_pontox1 = 0;
    double fib_pontoy1 = 0;
    java.util.List<org.jfree.chart.annotations.XYAnnotation> fib_preview; //o retracement de fibonacci se refere a um conjunto de linhas que sera desenhado

    void executaranotacao_fibonacci_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        if (fib_jatemponto1 == false)
        {
            fib_pontox1 = mcg_posmousex;
            fib_pontoy1 = mcg_posmousey;
            fib_jatemponto1 = true;
        } else if (fib_jatemponto1 == true)
        {
            anotacao_emhold = fib_preview;
            fib_jatemponto1 = false;
            fib_preview = null;
            fib_pontox1 = 0;
            fib_pontoy1 = 0;
        }
    }

    void executaranotacao_fibonacci_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {

        if (fib_jatemponto1 == false)
        {
        } else if (fib_jatemponto1 == true)
        {
            //remover annotation
            try
            {
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot();

                for (int i = 0; i < fib_preview.size(); i++)
                {
                    plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation) fib_preview.get(i));
                }
            } catch (Exception ex)
            {
            }

            //ja tendo o o primeiro ponto, pode desenhar o preview
            double fib_pontox2 = mcg_posmousex;
            double fib_pontoy2 = mcg_posmousey;

            //adicionar annotation line preview nova
            fib_preview = adicionarplotohlc_anotacaofibonacci(fib_pontox1, fib_pontoy1, fib_pontox2, fib_pontoy2);

        }
    }
    
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_anotacaofibonacci(double x1, double y1, double x2, double y2)
    {
        //funcao para desenhar fibonacci retracement no grafico

        //fibonacci retracement: https://www.investopedia.com/terms/f/fibonacciretracement.asp
        //deve ser desenhado linhas paralelas seguindo a sequencia de fibonacci
        //o usuario comeca com o ponto (x1,y1) que se refere a altura 0% do retracement e "x" de largura
        //as outras porcentagens cujas linhas devem ser desenhadas sao 23.6%, 38.2%, 50.0%, 61.8% e 100%
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) mcg_chart.getXYPlot();

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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcionamento Core - Crosshair (Anotacao Fixa)">
    java.util.List<org.jfree.chart.annotations.XYAnnotation> crosshair_preview;
    void executaranotacaofixa_crosshair_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {
        try
        {
            org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot();

            for (int i = 0; i < crosshair_preview.size(); i++)
            {
                plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation) crosshair_preview.get(i));
            }
        } 
        catch (Exception ex)
        {
        }

        crosshair_preview = adicionarplotohlc_anotacaofixacrosshair();
    }
    
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_anotacaofixacrosshair()
    {
        //funcao para desenhar um crosshair, canto esq-a-dir e canto inf-a-sup no ponto x e y do mouse
        /*
                       (LV_p1)
                          |
                          |
                          |
        (LH_p1)-----------X----(LH_p2)
        ~LH~              |
                          |
                       (LV_p2)~LV~
        */
        
        
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) mcg_chart.getXYPlot();
        
        //ponto central do crosshair
        double centrocrosshair_x = mcg_posmousex;
        double centrocrosshair_y = mcg_posmousey;
        
        //criando linha horizontal cinza
        double lh_p1_x = mcg_rangex.getLowerBound();
        double lh_p1_y = centrocrosshair_y;
        double lh_p2_x = mcg_rangex.getUpperBound();
        double lh_p2_y = centrocrosshair_y;
        org.jfree.chart.annotations.XYLineAnnotation xylh = new org.jfree.chart.annotations.XYLineAnnotation(lh_p1_x, lh_p1_y, lh_p2_x, lh_p2_y, new BasicStroke(0.4f), Color.RED);

        //criando linha vertical cinza
        double lv_p1_x = centrocrosshair_x;
        double lv_p1_y = mcg_rangey.getUpperBound();
        double lv_p2_x = centrocrosshair_x;
        double lv_p2_y = mcg_rangey.getLowerBound();
        org.jfree.chart.annotations.XYLineAnnotation xylv = new org.jfree.chart.annotations.XYLineAnnotation(lv_p1_x, lv_p1_y, lv_p2_x, lv_p2_y, new BasicStroke(0.4f), Color.RED);
        
        //texto preco
        String textopreco = String.format("%.4f", mcg_posmousey);
        org.jfree.chart.annotations.XYTextAnnotation xytextopreco = new org.jfree.chart.annotations.XYTextAnnotation(textopreco, lh_p1_x,centrocrosshair_y);
        xytextopreco.setTextAnchor(TextAnchor.TOP_LEFT);
        
        //texto data
        String datacrosshair = new java.util.Date((long) mcg_posmousex).toString();
        String textodata = datacrosshair;
        org.jfree.chart.annotations.XYTextAnnotation xytextodata = new org.jfree.chart.annotations.XYTextAnnotation(textodata, centrocrosshair_x, lv_p2_y);
        xytextodata.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        
        plot.addAnnotation(xylh);
        plot.addAnnotation(xylv);
        plot.addAnnotation(xytextopreco);
        plot.addAnnotation(xytextodata);

        java.util.List<org.jfree.chart.annotations.XYAnnotation> subannotations = new java.util.ArrayList<>();
        subannotations.add(xylh);
        subannotations.add(xylv);
        subannotations.add(xytextopreco);
        subannotations.add(xytextodata);

        return subannotations;
    }

    // </editor-fold>
    
    //</editor-fold>
    
    //funcao para adicionar ids referentes a subannotations de uma ferramenta na lista de controle
    public void adicionarplotohlc_anotacaoid(String idadicionar, java.util.List<org.jfree.chart.annotations.XYAnnotation> anotacao)
    {
        //esta funcao eh necessaria para adicionar o novo id a lista de anotacoes atuais
        //essa adicao deve ser feita manualmente pela logica do core de ferramentas, que funcionam com o mouse

        for (int i = 0; i < anotacao.size(); i++)
        {
            mcg_anotacoesIDS.add(idadicionar);
        }
    }

    //funcao para remover id da lista de ids de ferramentas
    public void removerplotohlc_anotacaoid(String idremover, int numerosubannotations)
    {
        for (int i = 0; i < numerosubannotations; i++)
        {
            mcg_anotacoesIDS.remove(idremover);
        }
    }

    //funcao para adicionar subannotations de uma ferramenta (utilizado ao realizar load de um asset)
    public void adicionarplotohlc_anotacao(java.util.List<org.jfree.chart.annotations.XYAnnotation> anotacao)
    {
        //funcao especial para carregamento de objetos de annotation
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) mcg_chart.getXYPlot();

        java.util.List<org.jfree.chart.annotations.XYAnnotation> anotacoesadicionar = (java.util.List<org.jfree.chart.annotations.XYAnnotation>) anotacao;
        org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot();
        for (int i = 0; i < anotacoesadicionar.size(); i++)
        {
            plot.addAnnotation(anotacoesadicionar.get(i));
        }
    }

    //funcao para remover subannotations do grafico ohlc
    public void removerplotohlc_anotacao(java.util.List<org.jfree.chart.annotations.XYAnnotation> anotacao)
    {
        //remover as subanotacoes graficas do chart
        org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot();
        for (int i = 0; i < anotacao.size(); i++)
        {
            plotatual.removeAnnotation(anotacao.get(i));
            //mierclasses.mcfuncoeshelper.mostrarmensagem("anotacao grafica removida");
        }
    }
    
    public void removerplotohlc_todasanotacoes()
    {
        org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot();
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
        timeseriesindicadoresohlc.addSeries(seriesadd);

    }

    public void adicionarplotohlc_indicadorid(String idindicador)
    {
        mcg_indicadoresIDS.add(idindicador);
    }

    public void removerplotohlc_indicador(String idindicador)
    {
        //encontra o dataset com o id do indicador para remove-lo do grafico
        for (int i = 0; i < mcg_indicadoresIDS.size(); i++)
        {
            String idindicadoratual = (String) mcg_indicadoresIDS.get(i);

            if (idindicador.equals(idindicadoratual))
            {
                timeseriesindicadoresohlc.removeSeries(i);
            }
        }
    }
    
    public void removerplotohlc_indicadorid(String idindicador)
    {
        //remove o idindicador da lista de controle
        mcg_indicadoresIDS.remove(idindicador);
    }

    public void removerplotohlc_indicadores()
    {
        //remover todos os indicadores do grafico
        timeseriesindicadoresohlc.removeAllSeries();
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes para recriar e retornar chart OHLC com indicadores">
    
    public void carregarohlc(java.util.List<mierclasses.mccandle> catual, String tituloohlc, String tipoescala, boolean resetaranotacoesindicadores)
    {
        //funcao utilizada para recarregar o ohlc mcg_chart e mcg_chartpanel
        //esta funcao pode querer resetar os indicadores e anotacoes do grafico, ou nao
        
        if (resetaranotacoesindicadores == true)
        {
            resetarlistaidsindicadores();
            resetarlistaidsanotacoes();
        }

        // <editor-fold defaultstate="collapsed" desc="associar miercandles novas a este MCG">
        mcg_candles = catual;
        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc="criar ohlcdataset com miercandles">
        org.jfree.data.xy.OHLCDataset olhcdataset = criarohlcdataset(mcg_candles, tituloohlc);
        //</editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="associar ohlcdataset a um novo jfreechart">
        //caso grafico linear
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
            mcg_chart = chart;
        } 
        //caso grafico log
        else if (tipoescala.equals("logaritmica"))
        {
            org.jfree.chart.axis.LogAxis rangeAxis = new org.jfree.chart.axis.LogAxis("");
            org.jfree.chart.renderer.xy.CandlestickRenderer renderer = new org.jfree.chart.renderer.xy.CandlestickRenderer();
            org.jfree.data.xy.XYDataset dataset = (org.jfree.data.xy.XYDataset) olhcdataset;
            org.jfree.chart.plot.XYPlot mainPlot = new org.jfree.chart.plot.XYPlot(dataset, domainAxis, rangeAxis, renderer);
            renderer.setSeriesPaint(0, Color.BLACK);
            renderer.setDrawVolume(true);
            org.jfree.chart.JFreeChart chart = new org.jfree.chart.JFreeChart(tituloohlc.toUpperCase(), null, mainPlot, false);
            mcg_chart = chart;
        }
        //</editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="criar um timeseriescollection, para receber timeseries de novos indicadores para este MCG">
        org.jfree.chart.renderer.xy.XYLineAndShapeRenderer rendereradd = new org.jfree.chart.renderer.xy.DefaultXYItemRenderer();
        rendereradd.setBaseShapesVisible(false);
        rendereradd.setBaseStroke(new BasicStroke(2.0f));
        org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) mcg_chart.getPlot();
        timeseriesindicadoresohlc = new org.jfree.data.time.TimeSeriesCollection();
        plotatual.setDataset(1, timeseriesindicadoresohlc);
        plotatual.setRenderer(1, rendereradd);
        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc="associar jfreechart a um novo chartpanel">
        org.jfree.chart.ChartPanel chartpanel = new org.jfree.chart.ChartPanel(mcg_chart);
        chartpanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener()
        {
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent e)
            {
                interpretaranotacao_mclick(e);
            }

            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent e)
            {
                interpretaranotacao_mmove(e);
            }
        });
        mcg_chartpanel = chartpanel;
        //</editor-fold>

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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes Helper">
    void resetarlistaidsindicadores()
    {
        mcg_indicadoresIDS = new java.util.ArrayList<>();
    }
    
    void resetarlistaidsanotacoes()
    {            
        mcg_anotacoesIDS = new java.util.ArrayList<>();
        mcg_anotacoesIDS.add("anotacaofixacrosshair_linhahorizontal");
        mcg_anotacoesIDS.add("anotacaofixacrosshair_linhavertical");
        mcg_anotacoesIDS.add("anotacaofixacrosshair_textopreco");
        mcg_anotacoesIDS.add("anotacaofixacrosshair_textodata");
    }
    
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> retornar_xyannotations_das_anotacoes()
    {
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) mcg_chart.getXYPlot();
        java.util.List<org.jfree.chart.annotations.XYAnnotation> listasubannotations = plot.getAnnotations();
        return listasubannotations;
    }
    //</editor-fold>
}
