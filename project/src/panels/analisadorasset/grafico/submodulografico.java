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
package panels.analisadorasset.grafico;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import mierclasses.mcfuncoeshelper;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

/**
 *
 * @author lucasmeyer
 */
public class submodulografico extends javax.swing.JPanel
{
    /*
    submodulo grafico, utilizado para análise técnica
    */

    // <editor-fold defaultstate="collapsed" desc="Variáveis Públicas">
    //analisador de asset pai, contem modulos para analises do asset (ateh o momento grafico e trader)
    public panels.analisadorasset.submodulosholder submodulohpai; 
    //classe utilizada para desenhar graficos
    public mierclasses.mcchartgenerator mcg; 
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="CONSTRUTOR">
    public submodulografico(panels.analisadorasset.submodulosholder shpai)
    {
        initComponents();
        
        submodulohpai = shpai;
        
        inicializarsubmodulografico();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Inicialização e Recarregamento">
    void inicializarsubmodulografico()
    {
        //popular objeto utilizado para desenho de graficos
        mcg = new mierclasses.mcchartgenerator();
        
        jPanelAnotacoes.setLayout(new java.awt.GridLayout(100,1));
        jPanelIndicadores.setLayout(new java.awt.GridLayout(100,1));

        //timer e timertask para atualizar range e info de graficos utilizados
        java.util.TimerTask chartrangeinfoupdater = new java.util.TimerTask() 
        {
            public void run() 
            {
                atualizarinformacoesposicaoatualgrafico();
                atualizarrangegraficosecundario(); 
            }
        };
        java.util.Timer timer_chartrangeinfoupdater = new java.util.Timer("chartrangeinfoupdater");
        long delay  = 100L;
        long period = 100L;
        timer_chartrangeinfoupdater.scheduleAtFixedRate(chartrangeinfoupdater, delay, period);
        
        //comecar mostrando dataset de teste e criar pela primeira vez o grafico
        jTextFieldNomeSimbolo.setText(submodulohpai.assetsimbolo);
        recarregardadossubmodulografico(true);
        
        //comecar mostrando painel inferior
        mostrarPainelInferior();
        
        this.validate();
        this.repaint();
    }
    
    //funcao responsavel por criar o grafico com o simbolo e periodo desejado pela primeira vez
    public void recarregardadossubmodulografico(boolean resetaranotacoesindicadores)
    {
        //funcao para carregar submodulo grafico, pode ser necessario resetar anotacoes e indicadores
        
        //<editor-fold defaultstate="collapsed" desc="Receber Candles do Asset Desejado">
        submodulohpai.assetsimbolo = jTextFieldNomeSimbolo.getText();
        String sourcesimboloescolhido = submodulohpai.assetsimbolo;
        String periodoescolhido = jComboBoxPeriodoSimbolo.getSelectedItem().toString();
        String escalagrafico = escalagraficoescolhido;
        
        
        java.util.List<mierclasses.mccandle> candles = null;
        
        if ((sourcesimboloescolhido.equals("testdata")) == true)
        {
            //codigo para criar um dataset offline para teste
            candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.offline_receberstockcandlessample();
        }
        else if ((sourcesimboloescolhido.equals("testdata")) == false)
        {
            if (((sourcesimboloescolhido.split(":")[0]).toLowerCase()).equals("iex"))
            {
                if (periodoescolhido.equals("1 Day"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.iex_receberstockcandleswithminutes((sourcesimboloescolhido.split(":")[1]), "1d");
                else if (periodoescolhido.equals("1 Month"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "1m");
                else if (periodoescolhido.equals("3 Months"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "3m");
                else if (periodoescolhido.equals("6 Months"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "6m");
                else if (periodoescolhido.equals("Year-to-date"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "ytd");
                else if (periodoescolhido.equals("1 Year"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "1y");
                else if (periodoescolhido.equals("2 Years"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "2y");
                else if (periodoescolhido.equals("5 Years"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "5y");
            } 
            else if (((sourcesimboloescolhido.split(":")[0]).toLowerCase()).equals("avs"))
            {
                if (periodoescolhido.equals("1 minute (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"1min","compact");
                else if (periodoescolhido.equals("1 minute (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"1min","full");
                else if (periodoescolhido.equals("5 minutes (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"5min","compact");
                else if (periodoescolhido.equals("5 minutes (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"5min","full");
                else if (periodoescolhido.equals("15 minutes (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"15min","compact");
                else if (periodoescolhido.equals("15 minutes (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"15min","full");
                else if (periodoescolhido.equals("30 minutes (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"30min","compact");
                else if (periodoescolhido.equals("30 minutes (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"30min","full");
                else if (periodoescolhido.equals("60 minutes (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"60min","compact");
                else if (periodoescolhido.equals("60 minutes (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"60min","full");
                else if (periodoescolhido.equals("Daily (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesdaily((sourcesimboloescolhido.split(":")[1]),"compact");
                 else if (periodoescolhido.equals("Daily (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesdaily((sourcesimboloescolhido.split(":")[1]),"full");
                else if (periodoescolhido.equals("Weekly"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesweekly((sourcesimboloescolhido.split(":")[1]));
                else if (periodoescolhido.equals("Monthly"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberstockcandlesmonthly((sourcesimboloescolhido.split(":")[1]));
            } 
            else if (((sourcesimboloescolhido.split(":")[0]).toLowerCase()).equals("avfx"))
            {
                String simbolo = (sourcesimboloescolhido.split(":")[1]);
                String fromsimbolo = (simbolo.split("\\.")[0]);
                String tosimbolo = (simbolo.split("\\.")[1]);
                
                if (periodoescolhido.equals("1 minute (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"1min","compact");
                else if (periodoescolhido.equals("1 minute (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"1min","full");
                else if (periodoescolhido.equals("5 minutes (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"5min","compact");
                else if (periodoescolhido.equals("5 minutes (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"5min","full");
                else if (periodoescolhido.equals("15 minutes (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"15min","compact");
                else if (periodoescolhido.equals("15 minutes (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"15min","full");
                else if (periodoescolhido.equals("30 minutes (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"30min","compact");
                else if (periodoescolhido.equals("30 minutes (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"30min","full");
                else if (periodoescolhido.equals("60 minutes (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"60min","compact");
                else if (periodoescolhido.equals("60 minutes (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"60min","full");
                else if (periodoescolhido.equals("Daily (Compact)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesdaily(fromsimbolo,tosimbolo,"compact");
                else if (periodoescolhido.equals("Daily (Full)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesdaily(fromsimbolo,tosimbolo,"full");
                else if (periodoescolhido.equals("Weekly"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesweekly(fromsimbolo,tosimbolo);
                else if (periodoescolhido.equals("Monthly"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.av_receberforexcandlesmonthly(fromsimbolo,tosimbolo);
            } 
            else if (((sourcesimboloescolhido.split(":")[0]).toLowerCase()).equals("crycom"))
            {
                if (periodoescolhido.equals("Minute (200)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "200", "Minute");
                else if (periodoescolhido.equals("Minute (1500)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "1500", "Minute");
                if (periodoescolhido.equals("Hourly (200)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "200", "Hourly");
                else if (periodoescolhido.equals("Hourly (1500)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "1500", "Hourly");
                if (periodoescolhido.equals("Daily (200)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "200", "Daily");
                else if (periodoescolhido.equals("Daily (1500)"))
                    candles = submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "1500", "Daily");
            } 
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Utilizar Candles para Recarregar OHLC">
        
        mcg.carregarohlc(candles,sourcesimboloescolhido + " (" + periodoescolhido + ")",escalagrafico,resetaranotacoesindicadores);
        org.jfree.chart.ChartPanel chartpanel = mcg.mcg_chartpanel;
        chartpanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener()
        {
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent e)
            {
                interpretarmouseclickchart(e);
            }

            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent e) 
            {
                interpretarmousemovechart(e);
            }
        });
        
        jPanelOHLCChartpanel.removeAll();
        jPanelOHLCChartpanel.setLayout(new java.awt.BorderLayout());
        jPanelOHLCChartpanel.add(chartpanel);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Remover Todas Anotações e Indicadores para o OHLC Novo - CASO RESET">
        if (resetaranotacoesindicadores == true)
        {
            removerGraficoIndicadorSecundario();
            jPanelIndicadores.removeAll();
            jPanelAnotacoes.removeAll();
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Recarregar Anotações e Indicadores para o OHLC Novo - CASO NAO RESET">
        else if (resetaranotacoesindicadores == false)
        {
            // <editor-fold defaultstate="collapsed" desc="Recarregar Dados e Plots dos Indicadores">
        
            //comecar limpando graficamente os timeseries dos indicadores
            mcg.removerplotohlc_indicadores();

            //atualizar dados de todos os indicadores e adiciona-los novamente (os seus ids ja existem)
            for (int i = 0; i < jPanelIndicadores.getComponentCount(); i++)
            {
                panels.analisadorasset.grafico.itemindicador novoindicador = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(i);
                String statusrunindicador = novoindicador.rodarscriptindicador();
                if (statusrunindicador.equals("ok"))
                {
                    if (novoindicador.mcbcindicador.tipoplot_lastrun.equals("drawoncandles"))
                    {
                        //considerando que o indicador rodou com sucesso, e que ele deve ser desenhado no ohlc,
                        //adicionar dados do indicador no grafico
                        mcg.adicionarplotohlc_indicador
                        (
                            novoindicador.mcbcindicador.pontosx_lastrun,
                            novoindicador.mcbcindicador.pontosy_lastrun,
                            novoindicador.mcbcindicador.tituloscript_lastrun
                        );

                        //recarregar grafico separado do indicador
                        novoindicador.recarregargraficoseparadoindicador();
                    }
                    else
                    {
                        //recarregar grafico separado do indicador
                        novoindicador.recarregargraficoseparadoindicador();
                    }
                }
                else
                {
                    mierclasses.mcfuncoeshelper.mostrarmensagem("Algum problema ocorreu e o indicador não pôde ser utilizado:\n\n" + statusrunindicador);
                }

            }

            //tambem recarregar grafico secundario
            for (int i = 0; i < jPanelIndicadores.getComponentCount(); i++)
            {
                    panels.analisadorasset.grafico.itemindicador miia = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(i);
                if (miia.chartseparadoembottom == true)
                {
                    //encontrado o indicador cujo grafico esta como secundario, tambem atualiza-lo
                    setarGraficoIndicadorSecundario(miia);
                    break;
                }
            }

            //</editor-fold>
        
            // <editor-fold defaultstate="collapsed" desc="Recarregar Dados e Plots das Anotacoes">
            
            //remover todos subannotations do OHLC
            mcg.removerplotohlc_todasanotacoes();

            //recarregar as subannotations dos itemsanotacao para o OHLC
            for (int i = 0; i < jPanelAnotacoes.getComponentCount(); i++)
            {
                panels.analisadorasset.grafico.itemanotacao novoanotacao = (panels.analisadorasset.grafico.itemanotacao)jPanelAnotacoes.getComponent(i);
                mcg.adicionarplotohlc_anotacao(novoanotacao.anotacao);
            }
            
            // </editor-fold>
        }
        //</editor-fold>
        
        this.validate();
        this.repaint(); 
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Main Section">
    
    public void adicionarsimboloaotextboxsubmodulo(String simboloadicionar)
    {
        //funcao para adicionar o codigo do simbolo no textbox de simbolo
        jTextFieldNomeSimbolo.setText(simboloadicionar);
    }
    
    public String escalagraficoescolhido = "linear";
    public void alternartipoescala(String escalaalternar)
    {
        if (escalaalternar.equals("linear"))
        {
            escalagraficoescolhido = "linear";
            jLabelLinearSwitch.setForeground(Color.blue);
            jLabelLogaritmicaSwitch.setForeground(Color.white);
        }
        else if (escalaalternar.equals("logaritmica"))
        {
            escalagraficoescolhido = "logaritmica";
            jLabelLinearSwitch.setForeground(Color.white);
            jLabelLogaritmicaSwitch.setForeground(Color.blue);
        }
    }
    

    
    public void atualizaropcoescomboboxperiodo()
    {
        //dependendo da source, iex, av, etc, o periodo muda na lista de periodos disponiveis
        String textoatualsimbolo = jTextFieldNomeSimbolo.getText();
        
        if ((textoatualsimbolo.equals("testdata")) == true)
        {
            jComboBoxPeriodoSimbolo.removeAllItems();
            jComboBoxPeriodoSimbolo.addItem("No options");
        }
        else if ((textoatualsimbolo.equals("testdata")) == false)
        {
            jComboBoxPeriodoSimbolo.removeAllItems();

            if (((textoatualsimbolo.split(":")[0]).toLowerCase()).equals("iex"))
            {
                jComboBoxPeriodoSimbolo.addItem("1 Day");
                jComboBoxPeriodoSimbolo.addItem("1 Month");
                jComboBoxPeriodoSimbolo.addItem("3 Months");
                jComboBoxPeriodoSimbolo.addItem("6 Months");
                jComboBoxPeriodoSimbolo.addItem("Year-to-date");
                jComboBoxPeriodoSimbolo.addItem("1 Year");
                jComboBoxPeriodoSimbolo.addItem("2 Years");
                jComboBoxPeriodoSimbolo.addItem("5 Years");
            }
            else if (((textoatualsimbolo.split(":")[0]).toLowerCase()).equals("avs"))
            {                
                jComboBoxPeriodoSimbolo.addItem("1 minute (Compact)");
                jComboBoxPeriodoSimbolo.addItem("1 minute (Full)");
                jComboBoxPeriodoSimbolo.addItem("5 minutes (Compact)");
                jComboBoxPeriodoSimbolo.addItem("5 minutes (Full)");
                jComboBoxPeriodoSimbolo.addItem("15 minutes (Compact)");
                jComboBoxPeriodoSimbolo.addItem("15 minutes (Full)");
                jComboBoxPeriodoSimbolo.addItem("30 minutes (Compact)");
                jComboBoxPeriodoSimbolo.addItem("30 minutes (Full)");
                jComboBoxPeriodoSimbolo.addItem("60 minutes (Compact)");
                jComboBoxPeriodoSimbolo.addItem("60 minutes (Full)");
                jComboBoxPeriodoSimbolo.addItem("Daily (Compact)");
                jComboBoxPeriodoSimbolo.addItem("Daily (Full)");
                jComboBoxPeriodoSimbolo.addItem("Weekly");
                jComboBoxPeriodoSimbolo.addItem("Monthly");
            }
            else if (((textoatualsimbolo.split(":")[0]).toLowerCase()).equals("avfx"))
            {                
                 jComboBoxPeriodoSimbolo.addItem("1 minute (Compact)");
                jComboBoxPeriodoSimbolo.addItem("1 minute (Full)");
                jComboBoxPeriodoSimbolo.addItem("5 minutes (Compact)");
                jComboBoxPeriodoSimbolo.addItem("5 minutes (Full)");
                jComboBoxPeriodoSimbolo.addItem("15 minutes (Compact)");
                jComboBoxPeriodoSimbolo.addItem("15 minutes (Full)");
                jComboBoxPeriodoSimbolo.addItem("30 minutes (Compact)");
                jComboBoxPeriodoSimbolo.addItem("30 minutes (Full)");
                jComboBoxPeriodoSimbolo.addItem("60 minutes (Compact)");
                jComboBoxPeriodoSimbolo.addItem("60 minutes (Full)");
                jComboBoxPeriodoSimbolo.addItem("Daily (Compact)");
                jComboBoxPeriodoSimbolo.addItem("Daily (Full)");
                jComboBoxPeriodoSimbolo.addItem("Weekly");
                jComboBoxPeriodoSimbolo.addItem("Monthly");
            }
            else if (((textoatualsimbolo.split(":")[0]).toLowerCase()).equals("crycom"))
            {                
                jComboBoxPeriodoSimbolo.addItem("Minute (200)");
                jComboBoxPeriodoSimbolo.addItem("Minute (1500)");
                jComboBoxPeriodoSimbolo.addItem("Hourly (200)");
                jComboBoxPeriodoSimbolo.addItem("Hourly (1500)");
                jComboBoxPeriodoSimbolo.addItem("Daily (200)");
                jComboBoxPeriodoSimbolo.addItem("Daily (1500)");
            }
            else
            {
                jComboBoxPeriodoSimbolo.addItem("No options");
            }
        }
        
        jComboBoxPeriodoSimbolo.setSelectedIndex(0);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Indicators Section">
        
    public void adicionarIndicadorNovo(String idbc, String paramsbc)
    {
        //funcao para adicionar novo item indicador a este submodulo grafico
        
        //criar item do indicador
        panels.analisadorasset.grafico.itemindicador novoindicador = new panels.analisadorasset.grafico.itemindicador(this, idbc, paramsbc);
        //rodar algoritmo do indicador
        String statusrunindicador = novoindicador.rodarscriptindicador();
        if (statusrunindicador.equals("ok"))
        {
            if (novoindicador.mcbcindicador.tipoplot_lastrun.equals("drawoncandles"))
            {
                //considerando que o indicador rodou com sucesso, e que ele deve ser desenhado no ohlc,
                //adicionar dados do indicador no grafico
                mcg.adicionarplotohlc_indicador
                (
                    novoindicador.mcbcindicador.pontosx_lastrun,
                    novoindicador.mcbcindicador.pontosy_lastrun,
                    novoindicador.mcbcindicador.tituloscript_lastrun
                );
                //adicionar id de controle no chart generator
                mcg.adicionarplotohlc_indicadorid(novoindicador.id);
                
                //o grafco tambem pode ser apresentado separadamente
                novoindicador.criargraficoseparadoindicador();
                novoindicador.recarregargraficoseparadoindicador();
            }
            else
            {
                //considerando que o grafico deve ser desenhado separadamente
                novoindicador.criargraficoseparadoindicador();
                novoindicador.recarregargraficoseparadoindicador();
            }

            
            //adicionar item no submodulo
            jPanelIndicadores.add(novoindicador);
            this.validate();
            this.repaint(); 
        }
        else
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("Algum problema ocorreu e o indicador não pôde ser utilizado:\n\n" + statusrunindicador);
        }

        //mcg.printlistaidsindicador();
    }
    
    public void adicionarIndicadorLoad(String nome, String id, String idbc, String paramsbc)
    {
        panels.analisadorasset.grafico.itemindicador novoindicador = new panels.analisadorasset.grafico.itemindicador(this, id, nome, idbc, paramsbc);
        String statusrunindicador = novoindicador.rodarscriptindicador();
        if (statusrunindicador.equals("ok"))
        {
            if (novoindicador.mcbcindicador.tipoplot_lastrun.equals("drawoncandles"))
            {
                //considerando que o indicador rodou com sucesso, e que ele deve ser desenhado no ohlc,
                //adicionar dados do indicador no grafico
                mcg.adicionarplotohlc_indicador
                (
                    novoindicador.mcbcindicador.pontosx_lastrun,
                    novoindicador.mcbcindicador.pontosy_lastrun,
                    novoindicador.mcbcindicador.tituloscript_lastrun
                );
                mcg.adicionarplotohlc_indicadorid(novoindicador.id);
                
                //o grafco tambem pode ser apresentado separadamente
                novoindicador.criargraficoseparadoindicador();
                novoindicador.recarregargraficoseparadoindicador();
            }
            else
            {
                //considerando que o grafico deve ser desenhado separadamente
                novoindicador.criargraficoseparadoindicador();
                novoindicador.recarregargraficoseparadoindicador();
            }

            jPanelIndicadores.add(novoindicador);
            this.validate();
            this.repaint(); 
        }
        else
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("Algum problema ocorreu e o indicador não pôde ser utilizado:\n\n" + statusrunindicador);
        }

    }

    public void removerIndicador(panels.analisadorasset.grafico.itemindicador mpiiremover)
    {
        if (mpiiremover.mcbcindicador.tipoplot_lastrun.equals("drawoncandles"))
        {
            mcg.removerplotohlc_indicador(mpiiremover.id);
            mcg.removerplotohlc_indicadorid(mpiiremover.id);
        }
        
        jPanelIndicadores.remove(mpiiremover);
        this.validate();
        this.repaint();
        
        //mcg.printlistaidsindicador();
    }

    public void setarGraficoIndicadorSecundario(panels.analisadorasset.grafico.itemindicador mpiidestacarabaixo)
    {
        for (int i = 0; i < jPanelIndicadores.getComponentCount(); i++)
        {
            panels.analisadorasset.grafico.itemindicador miia = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(i);
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String caminhoimagem = rootjar + "/outfiles/assets/upgray.png";
            miia.jLabelEscolherGraficoParaBottom.setIcon(new javax.swing.ImageIcon(caminhoimagem));
            miia.chartseparadoembottom = false;
        }
        
        //funcao que pega o grafico separado deste indicador e o separa para ficar logo abaixo do grafico ohlc
        //principal, tambem eh desejavel que quando o usuario estiver realizando zoom no grafico ohlc,
        //que o range do zoom do ohlc tambem passe para este grafico em questao
        jPanelSecondaryChartPanel.removeAll();
        jPanelSecondaryChartPanel.setLayout(new java.awt.BorderLayout());       
        jPanelSecondaryChartPanel.add
        (
            mpiidestacarabaixo.mfcs.retornarnovoplot_indicador
            (
                     mpiidestacarabaixo.mcbcindicador.pontosx_lastrun, 
                     mpiidestacarabaixo.mcbcindicador.pontosy_lastrun, 
                     mpiidestacarabaixo.mcbcindicador.tituloscript_lastrun,
                     mpiidestacarabaixo.mcbcindicador.tipoplot_lastrun
             )       
        );
        String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
        String caminhoimagem = rootjar + "/outfiles/assets/downgray.png";
        mpiidestacarabaixo.jLabelEscolherGraficoParaBottom.setIcon(new javax.swing.ImageIcon(caminhoimagem));
        mpiidestacarabaixo.chartseparadoembottom = true;

        jPanelSecondaryChartPanel.setVisible(true);
        jSplitPaneChartpanels.setDividerLocation(350);
        this.validate();
        this.repaint();
    }
    
    public void removerGraficoIndicadorSecundario()
    {
        jPanelSecondaryChartPanel.removeAll();
        
        for (int i = 0; i < jPanelIndicadores.getComponentCount(); i++)
        {
            panels.analisadorasset.grafico.itemindicador miia = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(i);
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String caminhoimagem = rootjar + "/outfiles/assets/upgray.png";
            miia.jLabelEscolherGraficoParaBottom.setIcon(new javax.swing.ImageIcon(caminhoimagem));
            miia.chartseparadoembottom = false;
        }
        
        jPanelSecondaryChartPanel.setVisible(false);
        this.validate();
        this.repaint();
    }
    

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Annotations Section">
    
    void adicionarAnotacaoNovo()
    {
        java.util.List<org.jfree.chart.annotations.XYAnnotation> lista_xyannotations = mcg.retornar_xyannotations_das_anotacoes();
        //quantidade de xyannotations presentes no grafico OHLC do MCG
        int quantidade_xyannotations_ohlc = lista_xyannotations.size();
        //quantidade de ids de anotacao na lista de controle do MCG
        int quantidadeids_listacontroleanotacao = mcg.mcg_anotacoesIDS.size();
        
        //caso a quantidade de quantidade_xyannotations_ohlc seja maior que quantidadeids_listacontroleanotacao
        //quer dizer que existem mais xyannotations no grafico OHLC, do que ids de controle, ou seja, existe uma anotacao nova, que contem uma nova lista de xyannotations
        if (quantidade_xyannotations_ohlc > quantidadeids_listacontroleanotacao)
        {
            //criar novo item anotacao
            panels.analisadorasset.grafico.itemanotacao novompia = new panels.analisadorasset.grafico.itemanotacao(this,mcg.ferramentaatualgrafico,mcg.anotacao_emhold);
            //associar item anotacao a lista de ids de anotacao de controle
            mcg.adicionarplotohlc_anotacaoid(novompia.id, novompia.anotacao);
            //adicionar item anotacao ao jpanel
            jPanelAnotacoes.add(novompia);    
        }
        
        this.validate();
        this.repaint();
    }
    
    public void adicionarAnotacaoLoad(String nome, String id, String tipo, java.util.List<org.jfree.chart.annotations.XYAnnotation> anotacao)
    {
        //funcao para adicionar nova anotacao em modo de carregamento
        
        //criar novo item de anotacao
        panels.analisadorasset.grafico.itemanotacao novompia = new panels.analisadorasset.grafico.itemanotacao(this, nome, id, tipo, anotacao);
        //adicionar anotacao (xyannotations list) no grafico
        mcg.adicionarplotohlc_anotacao(anotacao);
        //adicionar esta anotacao a lista de ids para controle do chartgenerator atual
        mcg.adicionarplotohlc_anotacaoid(novompia.id, novompia.anotacao);
        //adicionar item de anotacao grafica deste submodulo
        jPanelAnotacoes.add(novompia);
        
        this.validate();
        this.repaint();
    }
    
    public void removerAnotacao(panels.analisadorasset.grafico.itemanotacao mpiaremover)
    {
        //remover subanotacoes graficas desta anotacao do grafico
        mcg.removerplotohlc_anotacao(mpiaremover.anotacao);
        //remover todos os ids referentes a esta anotacao da lista de controle
        mcg.removerplotohlc_anotacaoid(mpiaremover.id, mpiaremover.anotacao.size());
        //remover item de anotacao grafica deste submodulo
        jPanelAnotacoes.remove(mpiaremover);
        
        this.validate();
        this.repaint();
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Tools and OHLC Section">
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes de interpretacao de mouse events do jfreechart">
    void interpretarmouseclickchart(org.jfree.chart.ChartMouseEvent e)
    {
        adicionarAnotacaoNovo();
    }
    
    void interpretarmousemovechart(org.jfree.chart.ChartMouseEvent e)
    {       
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funções de Suporte">
    void resetarcorbotoesferramentas()
    {
        jButtonAtivarSelecao.setForeground(Color.black);
        jButtonAtivarRegua.setForeground(Color.black);
        jButtonAtivarReta.setForeground(Color.black);
        jButtonAtivarFibonacci.setForeground(Color.black);
        jButtonAtivarTexto.setForeground(Color.black);
    }
        
    void atualizarinformacoesposicaoatualgrafico()
    {
        String valoryatualohlc = String.format("%.4f", mcg.mcg_posmousey);
        java.util.Date dataatualohlc = new java.util.Date((long)mcg.mcg_posmousex);
        //String valoratualrangex = String.format( "%.4f", mcg.rangex.getLength());
        //String valoratualrangey = String.format( "%.4f", mcg.rangey.getLength());
        jLabelInfo.setText("Price: " + valoryatualohlc + " Date: " + dataatualohlc.toString());   
    }
    
    void atualizarrangegraficosecundario()
    {
        //funcao para atualizar o range do grafico secundario, para ser igual ao do grafico primario
        
        if (jPanelSecondaryChartPanel.getComponentCount() == 1)
        {
            //org.jfree.chart.ChartPanel chartpanelohlc = mcg.chartpanelatual;
            //org.jfree.chart.JFreeChart chartohlc = chartpanelohlc.getChart();
            //org.jfree.chart.plot.XYPlot plotohlc = (org.jfree.chart.plot.XYPlot)chartohlc.getPlot();
            //org.jfree.data.Range rangexohlc = plotohlc.getDomainAxis().getRange();
            
            org.jfree.chart.ChartPanel chartpanelsecundario = (org.jfree.chart.ChartPanel)jPanelSecondaryChartPanel.getComponent(0);
            org.jfree.chart.JFreeChart chartsecundario = chartpanelsecundario.getChart();
            org.jfree.chart.plot.XYPlot plotsecundario = (org.jfree.chart.plot.XYPlot)chartsecundario.getPlot();
            org.jfree.data.Range rangexsecundario = plotsecundario.getDomainAxis().getRange();
            
            //plotsecundario.getDomainAxis().setRange(plotohlc.getDomainAxis().getRange());
            
            if (mcg.mcg_rangex != rangexsecundario)
            {
               plotsecundario.getDomainAxis().setRange(mcg.mcg_rangex); 
            }
        }
  
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Exportar Dados CSV">
    void exportarcsvdados()
    {
        //funcao para exportar arquivo .csv com candles e indicadores
        
         try
        {
        
            
            String csvSave = "";
            
            //comecar escrevendo header do documento
            csvSave = csvSave + "Timestamp;Date;Hour;Open;High;Low;Close;Volume";
            for (int j = 0; j < jPanelIndicadores.getComponentCount(); j++)
            {
                panels.analisadorasset.grafico.itemindicador indicadoratual = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(j);
                
                String nomeindicador = indicadoratual.mcbcindicador.tituloscript_lastrun;
                csvSave = csvSave + ";" + nomeindicador;
            }
            csvSave = csvSave + "\n";
            
            //escrever valores do documento
            java.util.List<mierclasses.mccandle> candles = mcg.mcg_candles;
            for (int i = 0; i < candles.size(); i++)
            {
                mierclasses.mccandle candleatual = candles.get(i);
                
                java.util.Date timestampatual = candleatual.timestampdate;
                String timestamp =  
                        (timestampatual.getYear()+1900) + "-" + 
                        (timestampatual.getMonth()+1) + "-" + 
                        timestampatual.getDate() + "-" + 
                        timestampatual.getHours() + "-" +
                        timestampatual.getMinutes()+ "-" +
                        timestampatual.getSeconds();
                
                String nomdate =  
                        (timestampatual.getMonth()+1) + "/" + 
                        timestampatual.getDate() + "/" + 
                        (timestampatual.getYear()+1900);
                        
                String nomhour = 
                        timestampatual.getHours() + ":" +
                        timestampatual.getMinutes()+ ":" +
                        timestampatual.getSeconds();
                
                double openatual = candleatual.opend;
                double highatual = candleatual.highd;
                double lowatual = candleatual.lowd;
                double closeatual = candleatual.closed;
                double volumeatual = candleatual.volumed;
                
                csvSave = csvSave + timestamp + ";" + nomdate + ";" + nomhour + ";" + String.valueOf(openatual)+ ";" + String.valueOf(highatual)+ ";" + String.valueOf(lowatual)+ ";" + String.valueOf(closeatual)+ ";" + String.valueOf(volumeatual);
                
                for (int j = 0; j < jPanelIndicadores.getComponentCount(); j++)
                {
                    panels.analisadorasset.grafico.itemindicador indicadoratual = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(j);
                    double valoryindicador = indicadoratual.retornarpontoy_dadotimestamp(timestampatual);  

                    if (valoryindicador != -30041993)
                        csvSave = csvSave + ";" + String.valueOf(valoryindicador);
                    else
                        csvSave = csvSave + ";";
                }
                csvSave = csvSave + "\n";
            }

            //abrir dialog para criar arquivo de save
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Please choose a location to export the .csv file");

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) 
            {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave + ".csv", "UTF-8");
                writer.println(csvSave);
                writer.close();
                mierclasses.mcfuncoeshelper.mostrarmensagem(".csv file exported.");
            }
            
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem occurred when exporting. Exception: " + ex.getMessage());
        }
        
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Tools and OHLC Section">
    boolean painelinferioraparecendo = false;
    public void mostrarPainelInferior()
    {
        String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
        String caminhoimagem = rootjar + "/outfiles/assets/downgray.png";
        jLabelPainelInferior.setIcon(new javax.swing.ImageIcon(caminhoimagem));
        jPanelInferior.setVisible(true);
        painelinferioraparecendo = true;

        
    }
    
    public void esconderPainelInferior()
    {
        String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
        String caminhoimagem = rootjar + "/outfiles/assets/upgray.png";
        jLabelPainelInferior.setIcon(new javax.swing.ImageIcon(caminhoimagem));
        jPanelInferior.setVisible(false);
        painelinferioraparecendo = false;
    } 
    //</editor-fold>
    
    // </editor-fold>


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanelFerramentasInfo = new javax.swing.JPanel();
        jButtonAtivarSelecao = new javax.swing.JButton();
        jButtonAtivarReta = new javax.swing.JButton();
        jButtonExportarCsv = new javax.swing.JButton();
        jButtonAtivarRegua = new javax.swing.JButton();
        jButtonAtivarTexto = new javax.swing.JButton();
        jButtonAtivarFibonacci = new javax.swing.JButton();
        jLabelPainelInferior = new javax.swing.JLabel();
        jPanelChartHolders = new javax.swing.JPanel();
        jSplitPaneChartpanels = new javax.swing.JSplitPane();
        jPanelOHLCChartpanel = new javax.swing.JPanel();
        jPanelSecondaryChartPanel = new javax.swing.JPanel();
        jPanelInferior = new javax.swing.JPanel();
        jPanelPrincipalPai = new javax.swing.JPanel();
        jLabelMain = new javax.swing.JLabel();
        jPanelPrincipal = new javax.swing.JPanel();
        jButtonEscolherSimbolo = new javax.swing.JButton();
        jLabelNomeSimbolo = new javax.swing.JLabel();
        jTextFieldNomeSimbolo = new javax.swing.JTextField();
        jLabelPeriodoSimbolo = new javax.swing.JLabel();
        jComboBoxPeriodoSimbolo = new javax.swing.JComboBox<>();
        jButtonCarregarConfiguracao = new javax.swing.JButton();
        jButtonSalvarConfiguracao = new javax.swing.JButton();
        jButtonAtualizarDadosGrafico = new javax.swing.JButton();
        jLabelLinearSwitch = new javax.swing.JLabel();
        jLabelLogaritmicaSwitch = new javax.swing.JLabel();
        jPanelIndicadoresPai = new javax.swing.JPanel();
        jButtonAdicionarIndicador = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelIndicadores = new javax.swing.JPanel();
        jLabelIndicadores = new javax.swing.JLabel();
        jPanelAnotacoesPai = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanelAnotacoes = new javax.swing.JPanel();
        jLabelAnotacoes = new javax.swing.JLabel();
        jLabelInfo = new javax.swing.JLabel();

        setBackground(new java.awt.Color(55, 55, 55));

        jPanelFerramentasInfo.setBackground(new java.awt.Color(35, 35, 35));

        jButtonAtivarSelecao.setForeground(new java.awt.Color(255, 0, 0));
        jButtonAtivarSelecao.setText("Select");
        jButtonAtivarSelecao.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarSelecaoActionPerformed(evt);
            }
        });

        jButtonAtivarReta.setText("Line");
        jButtonAtivarReta.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarRetaActionPerformed(evt);
            }
        });

        jButtonExportarCsv.setText("Export .csv");
        jButtonExportarCsv.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonExportarCsvActionPerformed(evt);
            }
        });

        jButtonAtivarRegua.setText("Ruler");
        jButtonAtivarRegua.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarReguaActionPerformed(evt);
            }
        });

        jButtonAtivarTexto.setText("Text");
        jButtonAtivarTexto.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarTextoActionPerformed(evt);
            }
        });

        jButtonAtivarFibonacci.setText("Fibonacci");
        jButtonAtivarFibonacci.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarFibonacciActionPerformed(evt);
            }
        });

        jLabelPainelInferior.setForeground(new java.awt.Color(125, 255, 255));
        jLabelPainelInferior.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPainelInferior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/downgray.png"))); // NOI18N
        jLabelPainelInferior.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelPainelInferiorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanelFerramentasInfoLayout = new javax.swing.GroupLayout(jPanelFerramentasInfo);
        jPanelFerramentasInfo.setLayout(jPanelFerramentasInfoLayout);
        jPanelFerramentasInfoLayout.setHorizontalGroup(
            jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFerramentasInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonAtivarSelecao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarTexto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarReta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarRegua)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarFibonacci)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExportarCsv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 789, Short.MAX_VALUE)
                .addComponent(jLabelPainelInferior, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelFerramentasInfoLayout.setVerticalGroup(
            jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFerramentasInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFerramentasInfoLayout.createSequentialGroup()
                        .addComponent(jLabelPainelInferior)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonAtivarSelecao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAtivarReta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonExportarCsv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAtivarRegua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAtivarTexto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAtivarFibonacci, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jSplitPaneChartpanels.setBackground(new java.awt.Color(55, 55, 55));
        jSplitPaneChartpanels.setDividerLocation(0);
        jSplitPaneChartpanels.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPaneChartpanels.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                jSplitPaneChartpanelsComponentResized(evt);
            }
        });

        jPanelOHLCChartpanel.setBackground(new java.awt.Color(0, 0, 0));
        jPanelOHLCChartpanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout jPanelOHLCChartpanelLayout = new javax.swing.GroupLayout(jPanelOHLCChartpanel);
        jPanelOHLCChartpanel.setLayout(jPanelOHLCChartpanelLayout);
        jPanelOHLCChartpanelLayout.setHorizontalGroup(
            jPanelOHLCChartpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelOHLCChartpanelLayout.setVerticalGroup(
            jPanelOHLCChartpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPaneChartpanels.setTopComponent(jPanelOHLCChartpanel);

        jPanelSecondaryChartPanel.setBackground(new java.awt.Color(45, 45, 45));

        javax.swing.GroupLayout jPanelSecondaryChartPanelLayout = new javax.swing.GroupLayout(jPanelSecondaryChartPanel);
        jPanelSecondaryChartPanel.setLayout(jPanelSecondaryChartPanelLayout);
        jPanelSecondaryChartPanelLayout.setHorizontalGroup(
            jPanelSecondaryChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSecondaryChartPanelLayout.setVerticalGroup(
            jPanelSecondaryChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPaneChartpanels.setBottomComponent(jPanelSecondaryChartPanel);

        javax.swing.GroupLayout jPanelChartHoldersLayout = new javax.swing.GroupLayout(jPanelChartHolders);
        jPanelChartHolders.setLayout(jPanelChartHoldersLayout);
        jPanelChartHoldersLayout.setHorizontalGroup(
            jPanelChartHoldersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPaneChartpanels)
        );
        jPanelChartHoldersLayout.setVerticalGroup(
            jPanelChartHoldersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPaneChartpanels, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
        );

        jPanelInferior.setBackground(new java.awt.Color(55, 55, 55));

        jPanelPrincipalPai.setBackground(new java.awt.Color(35, 35, 35));

        jLabelMain.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMain.setText("Main");

        jPanelPrincipal.setBackground(new java.awt.Color(120, 120, 120));

        jButtonEscolherSimbolo.setText("Search");
        jButtonEscolherSimbolo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonEscolherSimboloActionPerformed(evt);
            }
        });

        jLabelNomeSimbolo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelNomeSimbolo.setText("Symbol:");

        jTextFieldNomeSimbolo.setBackground(new java.awt.Color(125, 125, 125));
        jTextFieldNomeSimbolo.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldNomeSimbolo.addCaretListener(new javax.swing.event.CaretListener()
        {
            public void caretUpdate(javax.swing.event.CaretEvent evt)
            {
                jTextFieldNomeSimboloCaretUpdate(evt);
            }
        });

        jLabelPeriodoSimbolo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelPeriodoSimbolo.setText("Period:");

        jComboBoxPeriodoSimbolo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No options" }));

        jButtonCarregarConfiguracao.setText("Load Asset");
        jButtonCarregarConfiguracao.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonCarregarConfiguracaoActionPerformed(evt);
            }
        });

        jButtonSalvarConfiguracao.setText("Save Asset");
        jButtonSalvarConfiguracao.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonSalvarConfiguracaoActionPerformed(evt);
            }
        });

        jButtonAtualizarDadosGrafico.setText("Update");
        jButtonAtualizarDadosGrafico.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtualizarDadosGraficoActionPerformed(evt);
            }
        });

        jLabelLinearSwitch.setForeground(new java.awt.Color(0, 0, 255));
        jLabelLinearSwitch.setText("Linear");
        jLabelLinearSwitch.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelLinearSwitchMouseClicked(evt);
            }
        });

        jLabelLogaritmicaSwitch.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLogaritmicaSwitch.setText("Logarithmic");
        jLabelLogaritmicaSwitch.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelLogaritmicaSwitchMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanelPrincipalLayout = new javax.swing.GroupLayout(jPanelPrincipal);
        jPanelPrincipal.setLayout(jPanelPrincipalLayout);
        jPanelPrincipalLayout.setHorizontalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabelNomeSimbolo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldNomeSimbolo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEscolherSimbolo))
                    .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabelPeriodoSimbolo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxPeriodoSimbolo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabelLinearSwitch)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelLogaritmicaSwitch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAtualizarDadosGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jButtonCarregarConfiguracao, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonSalvarConfiguracao, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6))
        );
        jPanelPrincipalLayout.setVerticalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEscolherSimbolo)
                    .addComponent(jLabelNomeSimbolo)
                    .addComponent(jTextFieldNomeSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxPeriodoSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPeriodoSimbolo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAtualizarDadosGrafico)
                    .addComponent(jLabelLinearSwitch)
                    .addComponent(jLabelLogaritmicaSwitch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSalvarConfiguracao)
                    .addComponent(jButtonCarregarConfiguracao))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelPrincipalPaiLayout = new javax.swing.GroupLayout(jPanelPrincipalPai);
        jPanelPrincipalPai.setLayout(jPanelPrincipalPaiLayout);
        jPanelPrincipalPaiLayout.setHorizontalGroup(
            jPanelPrincipalPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelPrincipalPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelPrincipalPaiLayout.setVerticalGroup(
            jPanelPrincipalPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelMain, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelIndicadoresPai.setBackground(new java.awt.Color(35, 35, 35));

        jButtonAdicionarIndicador.setText("Add");
        jButtonAdicionarIndicador.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAdicionarIndicadorActionPerformed(evt);
            }
        });

        jPanelIndicadores.setBackground(new java.awt.Color(120, 120, 120));

        javax.swing.GroupLayout jPanelIndicadoresLayout = new javax.swing.GroupLayout(jPanelIndicadores);
        jPanelIndicadores.setLayout(jPanelIndicadoresLayout);
        jPanelIndicadoresLayout.setHorizontalGroup(
            jPanelIndicadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        jPanelIndicadoresLayout.setVerticalGroup(
            jPanelIndicadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 306, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelIndicadores);

        jLabelIndicadores.setForeground(new java.awt.Color(255, 255, 255));
        jLabelIndicadores.setText("Indicators");

        javax.swing.GroupLayout jPanelIndicadoresPaiLayout = new javax.swing.GroupLayout(jPanelIndicadoresPai);
        jPanelIndicadoresPai.setLayout(jPanelIndicadoresPaiLayout);
        jPanelIndicadoresPaiLayout.setHorizontalGroup(
            jPanelIndicadoresPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelIndicadoresPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelIndicadores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(68, 68, 68)
                .addComponent(jButtonAdicionarIndicador)
                .addContainerGap())
        );
        jPanelIndicadoresPaiLayout.setVerticalGroup(
            jPanelIndicadoresPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelIndicadoresPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelIndicadoresPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelIndicadores, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAdicionarIndicador, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        jPanelAnotacoesPai.setBackground(new java.awt.Color(35, 35, 35));

        jPanelAnotacoes.setBackground(new java.awt.Color(120, 120, 120));

        javax.swing.GroupLayout jPanelAnotacoesLayout = new javax.swing.GroupLayout(jPanelAnotacoes);
        jPanelAnotacoes.setLayout(jPanelAnotacoesLayout);
        jPanelAnotacoesLayout.setHorizontalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 858, Short.MAX_VALUE)
        );
        jPanelAnotacoesLayout.setVerticalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanelAnotacoes);

        jLabelAnotacoes.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAnotacoes.setText("Annotations");

        javax.swing.GroupLayout jPanelAnotacoesPaiLayout = new javax.swing.GroupLayout(jPanelAnotacoesPai);
        jPanelAnotacoesPai.setLayout(jPanelAnotacoesPaiLayout);
        jPanelAnotacoesPaiLayout.setHorizontalGroup(
            jPanelAnotacoesPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanelAnotacoesPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAnotacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelAnotacoesPaiLayout.setVerticalGroup(
            jPanelAnotacoesPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAnotacoesPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAnotacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelInferiorLayout = new javax.swing.GroupLayout(jPanelInferior);
        jPanelInferior.setLayout(jPanelInferiorLayout);
        jPanelInferiorLayout.setHorizontalGroup(
            jPanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInferiorLayout.createSequentialGroup()
                .addComponent(jPanelPrincipalPai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelIndicadoresPai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelAnotacoesPai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelInferiorLayout.setVerticalGroup(
            jPanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelIndicadoresPai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelPrincipalPai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelAnotacoesPai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabelInfo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelInfo.setText("-");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelChartHolders, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabelInfo))
                    .addComponent(jPanelFerramentasInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelInferior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelChartHolders, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelFerramentasInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAtivarRetaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarRetaActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarRetaActionPerformed
        mcg.trocarferramentaparareta();
        resetarcorbotoesferramentas();
        jButtonAtivarReta.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarRetaActionPerformed

    private void jButtonAtivarSelecaoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarSelecaoActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarSelecaoActionPerformed
        mcg.trocarferramentaparaselecao();
        resetarcorbotoesferramentas();
        jButtonAtivarSelecao.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarSelecaoActionPerformed

    private void jButtonAdicionarIndicadorActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAdicionarIndicadorActionPerformed
    {//GEN-HEADEREND:event_jButtonAdicionarIndicadorActionPerformed
        frames.analisadorasset.grafico.adicionarindicador mfai = new frames.analisadorasset.grafico.adicionarindicador(this);
        mfai.show();
    }//GEN-LAST:event_jButtonAdicionarIndicadorActionPerformed

    private void jButtonAtualizarDadosGraficoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtualizarDadosGraficoActionPerformed
    {//GEN-HEADEREND:event_jButtonAtualizarDadosGraficoActionPerformed
        submodulohpai.atualizardadosasset();
    }//GEN-LAST:event_jButtonAtualizarDadosGraficoActionPerformed

    private void jButtonEscolherSimboloActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonEscolherSimboloActionPerformed
    {//GEN-HEADEREND:event_jButtonEscolherSimboloActionPerformed
        frames.analisadorasset.grafico.adicionarsimbolo as = new frames.analisadorasset.grafico.adicionarsimbolo(this,jTextFieldNomeSimbolo.getText());
        as.show();    
    }//GEN-LAST:event_jButtonEscolherSimboloActionPerformed

    private void jButtonSalvarConfiguracaoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonSalvarConfiguracaoActionPerformed
    {//GEN-HEADEREND:event_jButtonSalvarConfiguracaoActionPerformed
        submodulohpai.salvardadosasset();
    }//GEN-LAST:event_jButtonSalvarConfiguracaoActionPerformed

    private void jButtonCarregarConfiguracaoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonCarregarConfiguracaoActionPerformed
    {//GEN-HEADEREND:event_jButtonCarregarConfiguracaoActionPerformed
        submodulohpai.carregardadosasset();
    }//GEN-LAST:event_jButtonCarregarConfiguracaoActionPerformed

    private void jLabelLinearSwitchMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelLinearSwitchMouseClicked
    {//GEN-HEADEREND:event_jLabelLinearSwitchMouseClicked
        alternartipoescala("linear");
    }//GEN-LAST:event_jLabelLinearSwitchMouseClicked

    private void jLabelLogaritmicaSwitchMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelLogaritmicaSwitchMouseClicked
    {//GEN-HEADEREND:event_jLabelLogaritmicaSwitchMouseClicked
        alternartipoescala("logaritmica");
    }//GEN-LAST:event_jLabelLogaritmicaSwitchMouseClicked

    private void jButtonExportarCsvActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonExportarCsvActionPerformed
    {//GEN-HEADEREND:event_jButtonExportarCsvActionPerformed

        exportarcsvdados();
    }//GEN-LAST:event_jButtonExportarCsvActionPerformed

    private void jButtonAtivarReguaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarReguaActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarReguaActionPerformed
        mcg.trocarferramentapararegua();
        resetarcorbotoesferramentas();
        jButtonAtivarRegua.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarReguaActionPerformed

    private void jButtonAtivarTextoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarTextoActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarTextoActionPerformed
        mcg.trocarferramentaparatexto();
        resetarcorbotoesferramentas();
        jButtonAtivarTexto.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarTextoActionPerformed

    private void jSplitPaneChartpanelsComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_jSplitPaneChartpanelsComponentResized
    {//GEN-HEADEREND:event_jSplitPaneChartpanelsComponentResized
        if (jPanelSecondaryChartPanel.getComponentCount() == 0)
        {
            //jSplitPaneChartpanels.setDividerLocation(500);
        }
    }//GEN-LAST:event_jSplitPaneChartpanelsComponentResized

    private void jButtonAtivarFibonacciActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarFibonacciActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarFibonacciActionPerformed
        mcg.trocarferramentaparafibonacci();
        resetarcorbotoesferramentas();
        jButtonAtivarFibonacci.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarFibonacciActionPerformed

    private void jTextFieldNomeSimboloCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_jTextFieldNomeSimboloCaretUpdate
    {//GEN-HEADEREND:event_jTextFieldNomeSimboloCaretUpdate
        atualizaropcoescomboboxperiodo();
    }//GEN-LAST:event_jTextFieldNomeSimboloCaretUpdate

    private void jLabelPainelInferiorMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelPainelInferiorMouseClicked
    {//GEN-HEADEREND:event_jLabelPainelInferiorMouseClicked
        if (painelinferioraparecendo == false)
        {
            mostrarPainelInferior();
        }
        else if (painelinferioraparecendo == true)
        {
            esconderPainelInferior();
        }
    }//GEN-LAST:event_jLabelPainelInferiorMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdicionarIndicador;
    private javax.swing.JButton jButtonAtivarFibonacci;
    private javax.swing.JButton jButtonAtivarRegua;
    private javax.swing.JButton jButtonAtivarReta;
    private javax.swing.JButton jButtonAtivarSelecao;
    private javax.swing.JButton jButtonAtivarTexto;
    private javax.swing.JButton jButtonAtualizarDadosGrafico;
    private javax.swing.JButton jButtonCarregarConfiguracao;
    private javax.swing.JButton jButtonEscolherSimbolo;
    private javax.swing.JButton jButtonExportarCsv;
    private javax.swing.JButton jButtonSalvarConfiguracao;
    public javax.swing.JComboBox<String> jComboBoxPeriodoSimbolo;
    private javax.swing.JLabel jLabelAnotacoes;
    private javax.swing.JLabel jLabelIndicadores;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelLinearSwitch;
    private javax.swing.JLabel jLabelLogaritmicaSwitch;
    private javax.swing.JLabel jLabelMain;
    private javax.swing.JLabel jLabelNomeSimbolo;
    public javax.swing.JLabel jLabelPainelInferior;
    private javax.swing.JLabel jLabelPeriodoSimbolo;
    public javax.swing.JPanel jPanelAnotacoes;
    private javax.swing.JPanel jPanelAnotacoesPai;
    private javax.swing.JPanel jPanelChartHolders;
    private javax.swing.JPanel jPanelFerramentasInfo;
    public javax.swing.JPanel jPanelIndicadores;
    private javax.swing.JPanel jPanelIndicadoresPai;
    private javax.swing.JPanel jPanelInferior;
    private javax.swing.JPanel jPanelOHLCChartpanel;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelPrincipalPai;
    private javax.swing.JPanel jPanelSecondaryChartPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPaneChartpanels;
    public javax.swing.JTextField jTextFieldNomeSimbolo;
    // End of variables declaration//GEN-END:variables
}
