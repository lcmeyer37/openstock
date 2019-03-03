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
package outrasopcoes.editorbearcodetraderbot;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author meyerlu
 */
public class frame_editorbearcodetraderbot extends javax.swing.JFrame 
{

    //janela utilizada para edicao de bearcode scripts
    
    // <editor-fold defaultstate="collapsed" desc="Variáveis">
    //tela principal pai
    public static main.frame_telaprincipal telappai;
    
    //classe interpretadora de bearcode (contem o codigo relacionado a este trader bot)
    public mierclasses.mcbctradingbotinterpreter mcbctraderbot;
    
    //offline trader utilizado por este submodulo
    public mierclasses.mcofflinetrader otrader; 
    
    //todas o set de candles de teste utilizado pelo editor
    java.util.List<mierclasses.mccandle> candlessample;
    //o subset de candles utilizados ao rodar a simulacao
    public java.util.List<mierclasses.mccandle> candlessimulacao;
    
    //handler para o output do jtextareaoutput
    mierclasses.mcjtextareahandler mcjtah;
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Construtor e Inicializar">
    public frame_editorbearcodetraderbot(main.frame_telaprincipal tppai) 
    {
        initComponents();
        
        telappai = tppai;

        inicializar();
    }
    
    void inicializar()
    {
        //popular traderbot interpretador de script
        mcbctraderbot = new mierclasses.mcbctradingbotinterpreter("testbctraderbot", "Teste Trader Bot", "", "");
        //adicionar handler para print de output pelo script
        mcjtah = new mierclasses.mcjtextareahandler(jTextAreaOutput);
        
        //receber as candles de teste como default
        jTextFieldNomeSimbolo.setText("testdata");
        atualizardadoscandlessimulador();
        
        //associar um offlinetrader para este editor (para realizar trades de simulacao)
        otrader = new mierclasses.mcofflinetrader(this);
        
        //resetar campos de edicao do editor com informacoes padrao
        resetarcamposeditor();
        
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Atualizar Dados do Simulador">
    void atualizardadoscandlessimulador()
    {
        String sourcesimboloescolhido = jTextFieldNomeSimbolo.getText();
        String periodoescolhido = jComboBoxPeriodoSimbolo.getSelectedItem().toString();
        
        //funcao utilizada para associar dados de candle ao simulador
        java.util.List<mierclasses.mccandle> candles = null;
        
        if ((sourcesimboloescolhido.equals("testdata")) == true)
        {
            //codigo para criar um dataset offline para teste
            candles = telappai.msapicomms.offline_receberstockcandlessample();
        }
        else if ((sourcesimboloescolhido.equals("testdata")) == false)
        {
            if (((sourcesimboloescolhido.split(":")[0]).toLowerCase()).equals("iex"))
            {
                if (periodoescolhido.equals("1 Day"))
                    candles = telappai.msapicomms.iex_receberstockcandleswithminutes((sourcesimboloescolhido.split(":")[1]), "1d");
                else if (periodoescolhido.equals("1 Month"))
                    candles = telappai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "1m");
                else if (periodoescolhido.equals("3 Months"))
                    candles = telappai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "3m");
                else if (periodoescolhido.equals("6 Months"))
                    candles = telappai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "6m");
                else if (periodoescolhido.equals("Year-to-date"))
                    candles = telappai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "ytd");
                else if (periodoescolhido.equals("1 Year"))
                    candles = telappai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "1y");
                else if (periodoescolhido.equals("2 Years"))
                    candles = telappai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "2y");
                else if (periodoescolhido.equals("5 Years"))
                    candles = telappai.msapicomms.iex_receberstockcandleswithoutminutes((sourcesimboloescolhido.split(":")[1]), "5y");
            } 
            else if (((sourcesimboloescolhido.split(":")[0]).toLowerCase()).equals("avs"))
            {
                if (periodoescolhido.equals("1 minute (Compact)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"1min","compact");
                else if (periodoescolhido.equals("1 minute (Full)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"1min","full");
                else if (periodoescolhido.equals("5 minutes (Compact)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"5min","compact");
                else if (periodoescolhido.equals("5 minutes (Full)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"5min","full");
                else if (periodoescolhido.equals("15 minutes (Compact)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"15min","compact");
                else if (periodoescolhido.equals("15 minutes (Full)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"15min","full");
                else if (periodoescolhido.equals("30 minutes (Compact)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"30min","compact");
                else if (periodoescolhido.equals("30 minutes (Full)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"30min","full");
                else if (periodoescolhido.equals("60 minutes (Compact)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"60min","compact");
                else if (periodoescolhido.equals("60 minutes (Full)"))
                    candles = telappai.msapicomms.av_receberstockcandlesintraday((sourcesimboloescolhido.split(":")[1]),"60min","full");
                else if (periodoescolhido.equals("Daily (Compact)"))
                    candles = telappai.msapicomms.av_receberstockcandlesdaily((sourcesimboloescolhido.split(":")[1]),"compact");
                 else if (periodoescolhido.equals("Daily (Full)"))
                    candles = telappai.msapicomms.av_receberstockcandlesdaily((sourcesimboloescolhido.split(":")[1]),"full");
                else if (periodoescolhido.equals("Weekly"))
                    candles = telappai.msapicomms.av_receberstockcandlesweekly((sourcesimboloescolhido.split(":")[1]));
                else if (periodoescolhido.equals("Monthly"))
                    candles = telappai.msapicomms.av_receberstockcandlesmonthly((sourcesimboloescolhido.split(":")[1]));
            } 
            else if (((sourcesimboloescolhido.split(":")[0]).toLowerCase()).equals("avfx"))
            {
                String simbolo = (sourcesimboloescolhido.split(":")[1]);
                String fromsimbolo = (simbolo.split("\\.")[0]);
                String tosimbolo = (simbolo.split("\\.")[1]);

                if (periodoescolhido.equals("1 minute (Compact)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"1min","compact");
                else if (periodoescolhido.equals("1 minute (Full)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"1min","full");
                else if (periodoescolhido.equals("5 minutes (Compact)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"5min","compact");
                else if (periodoescolhido.equals("5 minutes (Full)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"5min","full");
                else if (periodoescolhido.equals("15 minutes (Compact)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"15min","compact");
                else if (periodoescolhido.equals("15 minutes (Full)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"15min","full");
                else if (periodoescolhido.equals("30 minutes (Compact)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"30min","compact");
                else if (periodoescolhido.equals("30 minutes (Full)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"30min","full");
                else if (periodoescolhido.equals("60 minutes (Compact)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"60min","compact");
                else if (periodoescolhido.equals("60 minutes (Full)"))
                    candles = telappai.msapicomms.av_receberforexcandlesintraday(fromsimbolo,tosimbolo,"60min","full");
                else if (periodoescolhido.equals("Daily (Compact)"))
                    candles = telappai.msapicomms.av_receberforexcandlesdaily(fromsimbolo,tosimbolo,"compact");
                else if (periodoescolhido.equals("Daily (Full)"))
                    candles = telappai.msapicomms.av_receberforexcandlesdaily(fromsimbolo,tosimbolo,"full");
                else if (periodoescolhido.equals("Weekly"))
                    candles = telappai.msapicomms.av_receberforexcandlesweekly(fromsimbolo,tosimbolo);
                else if (periodoescolhido.equals("Monthly"))
                    candles = telappai.msapicomms.av_receberforexcandlesmonthly(fromsimbolo,tosimbolo);
            } 
            else if (((sourcesimboloescolhido.split(":")[0]).toLowerCase()).equals("crycom"))
            {
                if (periodoescolhido.equals("Minute (200)"))
                    candles = telappai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "200", "Minute");
                else if (periodoescolhido.equals("Minute (1500)"))
                    candles = telappai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "1500", "Minute");
                if (periodoescolhido.equals("Hourly (200)"))
                    candles = telappai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "200", "Hourly");
                else if (periodoescolhido.equals("Hourly (1500)"))
                    candles = telappai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "1500", "Hourly");
                if (periodoescolhido.equals("Daily (200)"))
                    candles = telappai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "200", "Daily");
                else if (periodoescolhido.equals("Daily (1500)"))
                    candles = telappai.msapicomms.crycom_recebercryptocandles((sourcesimboloescolhido.split(":")[1]), "1500", "Daily");
            } 
            
            
        }
        
        candlessample = candles;
        if (candlessample == null)
        {
            jLabelCandlesDataStatus.setText("Current Data: (none)");
            
            jComboBoxMinInterval.removeAllItems();
            jComboBoxMinInterval.addItem("(unavailable)");
            jComboBoxMaxInterval.removeAllItems();
            jComboBoxMaxInterval.addItem("(unavailable)");
            jComboBoxMinInterval.setSelectedIndex(0);
            jComboBoxMaxInterval.setSelectedIndex(0);
            
        }
        else
        {
            jLabelCandlesDataStatus.setText("Current Data: " + sourcesimboloescolhido.toUpperCase() + " (size: " + candlessample.size() + ")");
            
            jComboBoxMinInterval.removeAllItems();
            jComboBoxMaxInterval.removeAllItems();
            for (int i = 1; i < candlessample.size(); i++)
            {
                //i comeca de 1, porque eh necessario no minimo a candle em 0 para fazer o intervalo de 0 a 1
                //o min tambem deve sempre ser um valor <= ao maximo
                
                mierclasses.mccandle candleatual = candlessample.get(i);
                
                String dataadicionar = 
                        (candleatual.timestampdate.getMonth() + 1) + "/" +
                        candleatual.timestampdate.getDate() + "/" +
                        (candleatual.timestampdate.getYear() + 1900) + " " +
                        candleatual.timestampdate.getHours()+ ":" +
                        candleatual.timestampdate.getMinutes()+ ":" +
                        candleatual.timestampdate.getSeconds();
                        
                jComboBoxMinInterval.addItem(dataadicionar + " (" + (i) + ")");
                jComboBoxMaxInterval.addItem(dataadicionar + " (" + (i) + ")");
            }
            jComboBoxMinInterval.setSelectedIndex(jComboBoxMinInterval.getItemCount()-1);
            jComboBoxMaxInterval.setSelectedIndex(jComboBoxMinInterval.getItemCount()-1);
        }
        
    }
    
    //</editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="Rodar Script / Simulador / Export CSV">
    String rodarautotrade(String tradermove, String supportamount)
    {
        //mierclasses.mcfuncoeshelper.mostrarmensagem(tradermove + " " + supportamount);
        String statustrade = "erro - desconhecido";
        
        if (tradermove.equals("hold"))
        {
            //significa que nada deve ser feito
            statustrade = "ok";
        }
        else if (tradermove.equals("buyall"))
            statustrade =otrader.realizarcompratudo_basecotacao();
        else if (tradermove.equals("sellall"))
            statustrade = otrader.realizarvendatudo_basecotacao();
        else if (tradermove.equals("buyamount"))
            statustrade = otrader.realizarcompra_basecotacao(Double.valueOf(supportamount));
        else if (tradermove.equals("sellamount"))
            statustrade = otrader.realizarvenda_basecotacao(Double.valueOf(supportamount));
        
        return statustrade;
    }

    
    void rodarsimulacao()
    {
        //esta funcao pega as informacoes inputadas pelo usuario, e roda de acordo com o intervalo de simulacao
        //once|all -> rodar o script uma unica vez com todas as candles
        //multiple|5-7 -> rodar o script 3 vezes, utilizando desde um subset das candles[0-5] ate candles[0-7]
        
        //atualizar informacoes do trader com os campos do editor antes de iniciar simulacao
        atualizarinformacoes_editor_para_offlinetrader();
        
        String csvResultado = ""; //arquivo que sera exportado
        
        //comecar criando o header do csv
        csvResultado = csvResultado + 
                "First Timestamp (YYYY-MM-DD-HH-mm-ss);Last Timestamp (YYYY-MM-DD-HH-mm-ss);Last Close;Simulated Last Bid;Simulated Last Ask;Decision Now;Support Amount to Decision;Base Amount After Trade;Quote Amount After Trade;Total After Trade;Auto Trader Log;Debug Export";
        
        
        jTextAreaOutput.setText("");

        
        String minescolhido = jComboBoxMinInterval.getSelectedItem().toString();
        String maxescolhido = jComboBoxMaxInterval.getSelectedItem().toString();
        Integer numerominimo_sim = Integer.valueOf(((minescolhido.split("\\(")[1]).split("\\)"))[0]);
        Integer numeromaximo_sim = Integer.valueOf(((maxescolhido.split("\\(")[1]).split("\\)"))[0]);

        Integer numero_de_simulacoes = numeromaximo_sim - numerominimo_sim + 1;

        //rodar o algoritmo de simulacao varias vezes
        for (int i = 0; i < numero_de_simulacoes; i++)
        {
            Integer subsetmax_indice = i + numerominimo_sim;

            if (i == 0)
                jTextAreaOutput.setText("Simulation Interval [0 to " + subsetmax_indice + "]");
            else
                jTextAreaOutput.append("\n\nSimulation Interval [0 to " + subsetmax_indice + "]");


            //atualizar o subset de candles utilizado pela simulacao (neste caso o subset eh igual a todas as candles)
            //subList eh inclusive 0 e exclusive subsetmax_indice + 1
            candlessimulacao = candlessample.subList(0, subsetmax_indice + 1);

            //atualizar bid ask antes de rodar script
            otrader.atualizarbidask();

            //repopular parametros e codigo do script
            mcbctraderbot.atualizarscriptparametros(jTextAreaScript.getText(), jTextFieldParameters.getText());
            //rodar script
            String result = mcbctraderbot.rodarscript
            (
                    candlessimulacao,
                    otrader.quantidademoedabase, 
                    otrader.quantidademoedacotacao, 
                    otrader.melhorbid,
                    otrader.melhorask,
                    otrader.feecompra,
                    otrader.feevenda,
                    true,
                    mcjtah
            );


            String ultimo_logtrade = "nao ativo";
            if (result.equals("ok"))
            {
                mcjtah.print("\n======\nOK");

                if (jCheckBoxAutoTrade.isSelected() == true)
                {
                    String traderbot_move = (String)mcbctraderbot.respostatradermove_lastrun; 
                    String traderbot_supportamount = String.valueOf(((double[]) mcbctraderbot.respostaquantidadesuporte_lastrun)[0]);
                    ultimo_logtrade = rodarautotrade(traderbot_move,traderbot_supportamount);
                }
            }
            else
            {
                mcjtah.print("\n======\n" + "Exception: " + result);
                
                //se algum dos processos apresentar excecao, sair da simulacao
                return;
            }

            String primeiro_ts = retornartimestampcsv(candlessimulacao.get(0).timestampdate);
            String ultimo_ts = retornartimestampcsv(candlessimulacao.get(candlessimulacao.size()-1).timestampdate);
            String ultimo_close = String.valueOf(candlessimulacao.get(candlessimulacao.size()-1).closed);
            String ultimo_bid = String.valueOf(otrader.melhorbid);
            String ultimo_ask = String.valueOf(otrader.melhorask);
            String traderbot_move = (String)mcbctraderbot.respostatradermove_lastrun; 
            String traderbot_supportamount = String.valueOf(((double[]) mcbctraderbot.respostaquantidadesuporte_lastrun)[0]);
            String traderbot_debugexport = (String)mcbctraderbot.debugexport_lastrun; //variavel de suporte para print custom de interesse de script
            String postrade_baseamount = String.valueOf(otrader.quantidademoedabase);
            String postrade_quoteamount = String.valueOf(otrader.quantidademoedacotacao);
            String postrade_total = String.valueOf(otrader.totalfundos_moedacotacao());
            csvResultado = csvResultado + "\n" +
                primeiro_ts + ";" + ultimo_ts + ";" + ultimo_close + ";" + ultimo_bid + ";" + ultimo_ask + ";" + traderbot_move + ";" +
                traderbot_supportamount + ";" + postrade_baseamount + ";" + postrade_quoteamount + ";" + postrade_total + ";" + ultimo_logtrade + ";" + traderbot_debugexport;    
        }

        //apos rodar a simulacao, abrir frame_resultadosbearcodetraderbot para mostrar resultados da simulacao do editor em tabela,
        //mostrar uma opcao para exportar dados do csv, e mostrar um grafico de closes e decisao do robo
        frame_resultadosbearcodetraderbot frbctb = new frame_resultadosbearcodetraderbot(csvResultado);
        frbctb.show();
        

    }

    String retornartimestampcsv(java.util.Date timestampDate)
    {
        String timestampstring = 
                        (timestampDate.getYear()+1900) + "-" + 
                        (timestampDate.getMonth()+1) + "-" + 
                        timestampDate.getDate() + "-" + 
                        timestampDate.getHours() + "-" +
                        timestampDate.getMinutes()+ "-" +
                        timestampDate.getSeconds();
        
        return timestampstring;
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Carregar e Salvar Script">
    void salvararquivobcodeedicao()
    {
        try
        {
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Please choose a location and name for the Open Stock save file");

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) 
            {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave + ".bearcode", "UTF-8");
                writer.println(jTextAreaScript.getText());
                writer.close();
                
                jLabelCurrentFile.setText("Current File: " + fileToSave.getName() + ".bearcode");
                mierclasses.mcfuncoeshelper.mostrarmensagem("Bearcode saved.");
            }
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem occurred when saving. Exception: " + ex.getMessage());
        }
    }
    
    void carregararquivobcodeedicao()
    {
       try
       {
            //abrir janela para selecionar arquivo de save para carregar
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Please choose an Open Stock file to load");

            int userSelection = fileChooser.showOpenDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) 
            {
                
                java.io.File fileToLoad = null;

                try
                {
                    fileToLoad = fileChooser.getSelectedFile();
                    String scripttexto = mierclasses.mcfuncoeshelper.retornarStringArquivo(fileToLoad.getAbsolutePath());
                    
                    jTextAreaScript.setText(scripttexto);
                    jTextAreaOutput.setText("");
                    jLabelCurrentFile.setText("Current File: " + fileToLoad.getName());
                }
                catch (Exception ex)
                {
                    mierclasses.mcfuncoeshelper.mostrarmensagem(ex.getMessage());
                }
            }
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem occurred when loading. Exception: " + ex.getMessage());
        }
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes Helper">
    void resetarcamposeditor()
    {
        //recriar offline trader
        otrader.recriarofflinetrader("testdata");
        //setar alguns valores default para o otrader
        otrader.feecompra = 0.001;
        otrader.feevenda = 0.001;
        otrader.quantidademoedabase = 0;
        otrader.quantidademoedacotacao = 100000;
        
        //funcao para resetar script editor e associar os valores do offline trader com a gui
        String scriptdefault = "//bearcode trader bot sample\n" +
                "runoutput.print(\"sample code\");\n" +
                "\n" +
                "//sample candles for processing (data at /outfiles/samples/apple5y.json)\n" +
                "var candlesinput = candles;\n" +
                "\n" +
                "//sample values for funds in base and quote currency\n" +
                "var baseamountinput = basefunds;\n" +
                "var quoteamountinput = quotefunds;\n" +
                "var bidinput = lastbid;\n" +
                "var askinput = lastask;\n" +
                "var buyfeeinput = buyfee;\n" +
                "var sellfeeinput = sellfee;\n" +
                "\n" +
                "//parameters expected for processing\n" +
                "//var periodinput = parseInt(period);\n" +
                "\n" +
                "/*\n" +
                "(PROCESSING CODE)\n" +
                "available properties for candles: \n" +
                "var timestamp_string = candlesinput[0].timestampstr;\n" +
                "var open_string = candlesinput[0].openstr;\n" +
                "var high_string = candlesinput[0].highstr;\n" +
                "var close_string = candlesinput[0].closestr;\n" +
                "var low_string = candlesinput[0].lowstr;\n" +
                "var volume_string = candlesinput[0].volumestr;\n" +
                "var open_number = candlesinput[0].opend;\n" +
                "var high_number = candlesinput[0].highd;\n" +
                "var close_number = candlesinput[0].closed;\n" +
                "var low_number = candlesinput[0].lowd;\n" +
                "var volume_number = candlesinput[0].volumed;\n" +
                "var timestamp_date = candlesinput[0].timestampdate; \n" +
                "*/\n" +
                "\n" +
                "//example of return values for the script if decision is to buy 60 base currency\n" +
                "var tradermove = \"buyamount\"; //buy amount of base currency\n" +
                "var amountbase = [60]; //60 is the amount to buy\n" +
                "var supportamount = Java.to(amountbase,\"double[]\");";
                
        jTextAreaScript.setText(scriptdefault);
        jTextAreaScript.setCaretPosition(0);
        jTextAreaOutput.setText("");
        jTextAreaOutput.setCaretPosition(0);
        jTextFieldParameters.setText("");
        jLabelCurrentFile.setText("Current File: (new)");
        
        //atualizar informacoes relacionadas ao status atual do trader com a gui
        atualizarinformacoes_offlinetrader_para_editor();
    }
    
    void atualizarinformacoes_editor_para_offlinetrader()
    {
        //funcao para atualizar informacoes do offline trader com campos de edicao do editor
        otrader.feecompra = Double.valueOf(jTextFieldBuyFee.getText());
        otrader.feevenda = Double.valueOf(jTextFieldSellFee.getText());
        otrader.quantidademoedabase = Double.valueOf(jTextFieldBaseAmount.getText());
        otrader.quantidademoedacotacao = Double.valueOf(jTextFieldQuoteAmount.getText());
    }
    
    void atualizarinformacoes_offlinetrader_para_editor()
    {
        //funcao para atualizar informacoes do offline trader com campos de edicao do editor
        jTextFieldBuyFee.setText(String.valueOf(otrader.feecompra));
        jTextFieldSellFee.setText(String.valueOf(otrader.feevenda));
        jTextFieldBaseAmount.setText(String.valueOf(otrader.quantidademoedabase));
        jTextFieldQuoteAmount.setText(String.valueOf(otrader.quantidademoedacotacao));
    }
    
    String retornarstringposicaocaret()
    {
        try
        {
            int linenum = 1;
            int columnnum = 1;

            int caretpos = jTextAreaScript.getCaretPosition();
            linenum = jTextAreaScript.getLineOfOffset(caretpos);

            columnnum = caretpos - jTextAreaScript.getLineStartOffset(linenum) + 1;

            linenum += 1;
            
            return "(" + linenum + ":" + columnnum + ")";
        }
        catch (Exception ex)
        {
            return "";
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
    //</editor-fold>
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanelPai = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaScript = new javax.swing.JTextArea();
        jLabelScript = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaOutput = new javax.swing.JTextArea();
        jLabelOutput = new javax.swing.JLabel();
        jButtonSaveFile = new javax.swing.JButton();
        jButtonLoadFile = new javax.swing.JButton();
        jLabelCurrentFile = new javax.swing.JLabel();
        jButtonResetEditor = new javax.swing.JButton();
        jLabelCaretPosition = new javax.swing.JLabel();
        jLabelBuyFee = new javax.swing.JLabel();
        jTextFieldBuyFee = new javax.swing.JTextField();
        jLabelParameters = new javax.swing.JLabel();
        jTextFieldParameters = new javax.swing.JTextField();
        jLabelSellFee = new javax.swing.JLabel();
        jTextFieldSellFee = new javax.swing.JTextField();
        jLabelTestParameters3 = new javax.swing.JLabel();
        jTextFieldBaseAmount = new javax.swing.JTextField();
        jLabelBaseAmount = new javax.swing.JLabel();
        jLabelQuoteAmount = new javax.swing.JLabel();
        jTextFieldQuoteAmount = new javax.swing.JTextField();
        jButtonRun = new javax.swing.JButton();
        jCheckBoxAutoTrade = new javax.swing.JCheckBox();
        jPanelChooseDataSimulator = new javax.swing.JPanel();
        jComboBoxPeriodoSimbolo = new javax.swing.JComboBox<>();
        jLabelPeriodoSimbolo = new javax.swing.JLabel();
        jTextFieldNomeSimbolo = new javax.swing.JTextField();
        jLabelNomeSimbolo = new javax.swing.JLabel();
        jLabelCandlesDataStatus = new javax.swing.JLabel();
        jButtonRecarregarCandlesData = new javax.swing.JButton();
        jComboBoxMinInterval = new javax.swing.JComboBox<>();
        jComboBoxMaxInterval = new javax.swing.JComboBox<>();
        jLabelBuyFee1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bot Editor and Simulator");

        jPanelPai.setBackground(new java.awt.Color(55, 55, 55));

        jTextAreaScript.setBackground(new java.awt.Color(235, 235, 235));
        jTextAreaScript.setColumns(20);
        jTextAreaScript.setFont(new java.awt.Font("Consolas", 0, 16)); // NOI18N
        jTextAreaScript.setRows(5);
        jTextAreaScript.addCaretListener(new javax.swing.event.CaretListener()
        {
            public void caretUpdate(javax.swing.event.CaretEvent evt)
            {
                jTextAreaScriptCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(jTextAreaScript);

        jLabelScript.setForeground(new java.awt.Color(255, 255, 255));
        jLabelScript.setText("Script");

        jTextAreaOutput.setEditable(false);
        jTextAreaOutput.setBackground(new java.awt.Color(35, 35, 35));
        jTextAreaOutput.setColumns(20);
        jTextAreaOutput.setFont(new java.awt.Font("Consolas", 0, 13)); // NOI18N
        jTextAreaOutput.setForeground(new java.awt.Color(255, 255, 255));
        jTextAreaOutput.setRows(5);
        jTextAreaOutput.setFocusable(false);
        jScrollPane2.setViewportView(jTextAreaOutput);

        jLabelOutput.setForeground(new java.awt.Color(255, 255, 255));
        jLabelOutput.setText("Output");

        jButtonSaveFile.setText("Save File");
        jButtonSaveFile.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonSaveFileActionPerformed(evt);
            }
        });

        jButtonLoadFile.setText("Load File");
        jButtonLoadFile.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonLoadFileActionPerformed(evt);
            }
        });

        jLabelCurrentFile.setForeground(new java.awt.Color(255, 255, 255));
        jLabelCurrentFile.setText("Current File: (new)");

        jButtonResetEditor.setForeground(new java.awt.Color(255, 0, 0));
        jButtonResetEditor.setText("Reset");
        jButtonResetEditor.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonResetEditorActionPerformed(evt);
            }
        });

        jLabelCaretPosition.setForeground(new java.awt.Color(255, 255, 255));
        jLabelCaretPosition.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);

        jLabelBuyFee.setForeground(new java.awt.Color(255, 255, 255));
        jLabelBuyFee.setText("Buy Fee:");

        jTextFieldBuyFee.setBackground(new java.awt.Color(0, 0, 0));
        jTextFieldBuyFee.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldBuyFee.setText("0.001");
        jTextFieldBuyFee.setCaretColor(new java.awt.Color(125, 125, 125));
        jTextFieldBuyFee.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jTextFieldBuyFeeActionPerformed(evt);
            }
        });

        jLabelParameters.setForeground(new java.awt.Color(255, 255, 255));
        jLabelParameters.setText("Test Parameters:");

        jTextFieldParameters.setBackground(new java.awt.Color(0, 0, 0));
        jTextFieldParameters.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldParameters.setCaretColor(new java.awt.Color(125, 125, 125));

        jLabelSellFee.setForeground(new java.awt.Color(255, 255, 255));
        jLabelSellFee.setText("Sell Fee:");

        jTextFieldSellFee.setBackground(new java.awt.Color(0, 0, 0));
        jTextFieldSellFee.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldSellFee.setText("0.001");
        jTextFieldSellFee.setCaretColor(new java.awt.Color(125, 125, 125));

        jLabelTestParameters3.setForeground(new java.awt.Color(255, 255, 255));

        jTextFieldBaseAmount.setBackground(new java.awt.Color(0, 0, 0));
        jTextFieldBaseAmount.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldBaseAmount.setText("0");
        jTextFieldBaseAmount.setCaretColor(new java.awt.Color(125, 125, 125));

        jLabelBaseAmount.setForeground(new java.awt.Color(255, 255, 255));
        jLabelBaseAmount.setText("Current Base Amount:");

        jLabelQuoteAmount.setForeground(new java.awt.Color(255, 255, 255));
        jLabelQuoteAmount.setText("Current Quote Amount:");

        jTextFieldQuoteAmount.setBackground(new java.awt.Color(0, 0, 0));
        jTextFieldQuoteAmount.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldQuoteAmount.setText("100000");
        jTextFieldQuoteAmount.setCaretColor(new java.awt.Color(125, 125, 125));

        jButtonRun.setForeground(new java.awt.Color(0, 0, 255));
        jButtonRun.setText("Run");
        jButtonRun.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonRunActionPerformed(evt);
            }
        });

        jCheckBoxAutoTrade.setBackground(new java.awt.Color(55, 55, 55));
        jCheckBoxAutoTrade.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxAutoTrade.setSelected(true);
        jCheckBoxAutoTrade.setText("Auto Trade");

        jPanelChooseDataSimulator.setBackground(new java.awt.Color(35, 35, 35));

        jComboBoxPeriodoSimbolo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No options" }));

        jLabelPeriodoSimbolo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelPeriodoSimbolo.setText("Period:");

        jTextFieldNomeSimbolo.setBackground(new java.awt.Color(125, 125, 125));
        jTextFieldNomeSimbolo.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldNomeSimbolo.addCaretListener(new javax.swing.event.CaretListener()
        {
            public void caretUpdate(javax.swing.event.CaretEvent evt)
            {
                jTextFieldNomeSimboloCaretUpdate(evt);
            }
        });

        jLabelNomeSimbolo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelNomeSimbolo.setText("Symbol:");

        jLabelCandlesDataStatus.setForeground(new java.awt.Color(255, 255, 255));
        jLabelCandlesDataStatus.setText("Current Data:");

        jButtonRecarregarCandlesData.setText("Load Data");
        jButtonRecarregarCandlesData.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonRecarregarCandlesDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelChooseDataSimulatorLayout = new javax.swing.GroupLayout(jPanelChooseDataSimulator);
        jPanelChooseDataSimulator.setLayout(jPanelChooseDataSimulatorLayout);
        jPanelChooseDataSimulatorLayout.setHorizontalGroup(
            jPanelChooseDataSimulatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelChooseDataSimulatorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelNomeSimbolo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldNomeSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPeriodoSimbolo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxPeriodoSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRecarregarCandlesData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelCandlesDataStatus)
                .addContainerGap())
        );
        jPanelChooseDataSimulatorLayout.setVerticalGroup(
            jPanelChooseDataSimulatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChooseDataSimulatorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelChooseDataSimulatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelNomeSimbolo)
                    .addComponent(jTextFieldNomeSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPeriodoSimbolo)
                    .addComponent(jComboBoxPeriodoSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCandlesDataStatus)
                    .addComponent(jButtonRecarregarCandlesData))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jComboBoxMinInterval.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(unavailable)" }));

        jComboBoxMaxInterval.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(unavailable)" }));

        jLabelBuyFee1.setForeground(new java.awt.Color(255, 255, 255));
        jLabelBuyFee1.setText("Interval:");

        javax.swing.GroupLayout jPanelPaiLayout = new javax.swing.GroupLayout(jPanelPai);
        jPanelPai.setLayout(jPanelPaiLayout);
        jPanelPaiLayout.setHorizontalGroup(
            jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jButtonResetEditor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelCurrentFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonLoadFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSaveFile))
                    .addGroup(jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jLabelScript)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelCaretPosition, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTestParameters3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jLabelParameters)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldParameters))
                    .addGroup(jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jLabelOutput)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addGap(0, 89, Short.MAX_VALUE)
                        .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                                .addComponent(jLabelBuyFee1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxMinInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxMaxInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxAutoTrade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonRun))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                                .addComponent(jLabelBuyFee)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldBuyFee, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabelSellFee)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldSellFee, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelBaseAmount)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldBaseAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelQuoteAmount)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldQuoteAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
            .addComponent(jPanelChooseDataSimulator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelPaiLayout.setVerticalGroup(
            jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPaiLayout.createSequentialGroup()
                .addComponent(jPanelChooseDataSimulator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelScript)
                    .addComponent(jLabelCaretPosition))
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPaiLayout.createSequentialGroup()
                        .addGap(195, 195, 195)
                        .addComponent(jLabelTestParameters3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE))
                    .addGroup(jPanelPaiLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelParameters)
                    .addComponent(jTextFieldParameters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBuyFee)
                    .addComponent(jLabelSellFee)
                    .addComponent(jTextFieldSellFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldBuyFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldBaseAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelBaseAmount)
                    .addComponent(jTextFieldQuoteAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelQuoteAmount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRun)
                    .addComponent(jCheckBoxAutoTrade)
                    .addComponent(jComboBoxMinInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxMaxInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelBuyFee1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelOutput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSaveFile)
                    .addComponent(jButtonLoadFile)
                    .addComponent(jButtonResetEditor)
                    .addComponent(jLabelCurrentFile))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextAreaScriptCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextAreaScriptCaretUpdate
        jLabelCaretPosition.setText(retornarstringposicaocaret());
    }//GEN-LAST:event_jTextAreaScriptCaretUpdate

    private void jButtonResetEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetEditorActionPerformed
        resetarcamposeditor();
    }//GEN-LAST:event_jButtonResetEditorActionPerformed

    private void jButtonLoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoadFileActionPerformed
        carregararquivobcodeedicao();
    }//GEN-LAST:event_jButtonLoadFileActionPerformed

    private void jButtonSaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveFileActionPerformed
        salvararquivobcodeedicao();
    }//GEN-LAST:event_jButtonSaveFileActionPerformed

    private void jTextFieldBuyFeeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTextFieldBuyFeeActionPerformed
    {//GEN-HEADEREND:event_jTextFieldBuyFeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBuyFeeActionPerformed

    private void jTextFieldNomeSimboloCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_jTextFieldNomeSimboloCaretUpdate
    {//GEN-HEADEREND:event_jTextFieldNomeSimboloCaretUpdate
        atualizaropcoescomboboxperiodo();
    }//GEN-LAST:event_jTextFieldNomeSimboloCaretUpdate

    private void jButtonRecarregarCandlesDataActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonRecarregarCandlesDataActionPerformed
    {//GEN-HEADEREND:event_jButtonRecarregarCandlesDataActionPerformed
        atualizardadoscandlessimulador();
    }//GEN-LAST:event_jButtonRecarregarCandlesDataActionPerformed

    private void jButtonRunActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonRunActionPerformed
    {//GEN-HEADEREND:event_jButtonRunActionPerformed
        rodarsimulacao();
        atualizarinformacoes_offlinetrader_para_editor();
    }//GEN-LAST:event_jButtonRunActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frame_editorbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frame_editorbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frame_editorbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frame_editorbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frame_editorbearcodetraderbot(telappai).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLoadFile;
    private javax.swing.JButton jButtonRecarregarCandlesData;
    private javax.swing.JButton jButtonResetEditor;
    private javax.swing.JButton jButtonRun;
    private javax.swing.JButton jButtonSaveFile;
    private javax.swing.JCheckBox jCheckBoxAutoTrade;
    private javax.swing.JComboBox<String> jComboBoxMaxInterval;
    private javax.swing.JComboBox<String> jComboBoxMinInterval;
    public javax.swing.JComboBox<String> jComboBoxPeriodoSimbolo;
    private javax.swing.JLabel jLabelBaseAmount;
    private javax.swing.JLabel jLabelBuyFee;
    private javax.swing.JLabel jLabelBuyFee1;
    private javax.swing.JLabel jLabelCandlesDataStatus;
    private javax.swing.JLabel jLabelCaretPosition;
    private javax.swing.JLabel jLabelCurrentFile;
    private javax.swing.JLabel jLabelNomeSimbolo;
    private javax.swing.JLabel jLabelOutput;
    private javax.swing.JLabel jLabelParameters;
    private javax.swing.JLabel jLabelPeriodoSimbolo;
    private javax.swing.JLabel jLabelQuoteAmount;
    private javax.swing.JLabel jLabelScript;
    private javax.swing.JLabel jLabelSellFee;
    private javax.swing.JLabel jLabelTestParameters3;
    private javax.swing.JPanel jPanelChooseDataSimulator;
    private javax.swing.JPanel jPanelPai;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaOutput;
    private javax.swing.JTextArea jTextAreaScript;
    private javax.swing.JTextField jTextFieldBaseAmount;
    private javax.swing.JTextField jTextFieldBuyFee;
    public javax.swing.JTextField jTextFieldNomeSimbolo;
    private javax.swing.JTextField jTextFieldParameters;
    private javax.swing.JTextField jTextFieldQuoteAmount;
    private javax.swing.JTextField jTextFieldSellFee;
    // End of variables declaration//GEN-END:variables
}