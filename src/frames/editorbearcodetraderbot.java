/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frames;

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
public class editorbearcodetraderbot extends javax.swing.JFrame 
{

    //janela utilizada para edicao de bearcode scripts
    
    //tela principal pai
    public static main.TelaPrincipal telappai;
    
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
    
    public editorbearcodetraderbot(main.TelaPrincipal tppai) 
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
        
        //receber as candles de teste
        candlessample = telappai.msapicomms.receberstockchartsample();
        
        //associar um offlinetrader para este editor (para realizar trades de simulacao)
        otrader = new mierclasses.mcofflinetrader(this);
        
        //resetar campos de edicao do editor com informacoes padrao
        resetarcamposeditor();
        
    }
    
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
        jTextFieldSimulationInterval.setText("once/all");
        jLabelCandlesDataSize.setText("Candles Data Size: " + candlessample.size());
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

    
    void rodarsimulacao(boolean exportarcsv)
    {
        //esta funcao pega as informacoes inputadas pelo usuario, e roda de acordo com o intervalo de simulacao
        //once|all -> rodar o script uma unica vez com todas as candles
        //multiple|5-7 -> rodar o script 3 vezes, utilizando desde um subset das candles[0-5] ate candles[0-7]
        
        //atualizar informacoes do trader com os campos do editor antes de iniciar simulacao
        atualizarinformacoes_editor_para_offlinetrader();
        
        String csvSave = ""; //arquivo que sera exportado
        
        //comecar criando o header do csv
        csvSave = csvSave + 
                "First Timestamp (YYYY-MM-DD-HH-mm-ss);Last Timestamp (YYYY-MM-DD-HH-mm-ss);Last Close;Simulated Last Bid;Simulated Last Ask;Decision Now;Support Amount to Decision;Base Amount After Trade;Quote Amount After Trade;Total After Trade;Auto Trader Log (pt)";
        
        
        jTextAreaOutput.setText("");
        String tiposimulacao = jTextFieldSimulationInterval.getText();
        
        if (tiposimulacao.equals("once/all"))
        {
            jTextAreaOutput.setText("Simulation ONCE/ALL");
            
            //atualizar o subset de candles utilizado pela simulacao (neste caso o subset eh igual a todas as candles)
            candlessimulacao = candlessample;
            
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

            //string utilizada para log do trader
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
            }
            
            
            //adicionar o intervalo utilizado na planilha e o resultado da simulacao

            String primeiro_ts = retornartimestampcsv(candlessimulacao.get(0).timestampdate);
            String ultimo_ts = retornartimestampcsv(candlessimulacao.get(candlessimulacao.size()-1).timestampdate);
            String ultimo_close = String.valueOf(candlessimulacao.get(candlessimulacao.size()-1).closed);
            String ultimo_bid = String.valueOf(otrader.melhorbid);
            String ultimo_ask = String.valueOf(otrader.melhorask);
            String traderbot_move = (String)mcbctraderbot.respostatradermove_lastrun; 
            String traderbot_supportamount = String.valueOf(((double[]) mcbctraderbot.respostaquantidadesuporte_lastrun)[0]);
            String postrade_baseamount = String.valueOf(otrader.quantidademoedabase);
            String postrade_quoteamount = String.valueOf(otrader.quantidademoedacotacao);
            String postrade_total = String.valueOf(otrader.totalfundos_moedacotacao());
            csvSave = csvSave + "\n" +
                primeiro_ts + ";" + ultimo_ts + ";" + ultimo_close + ";" + ultimo_bid + ";" + ultimo_ask + ";" + traderbot_move + ";" +
                    traderbot_supportamount + ";" + postrade_baseamount + ";" + postrade_quoteamount + ";" + postrade_total + ";" + ultimo_logtrade;
                    
        }
        else
        {
            //rodar simulacao caso multiple/inicio-fim
            
            //mierclasses.mcfuncoeshelper.mostrarmensagem(tiposimulacao);
            String intervalosimulacao = tiposimulacao.split("/")[1];
            //mierclasses.mcfuncoeshelper.mostrarmensagem(intervalosimulacao);
            Integer numerominimo_sim = Integer.valueOf(intervalosimulacao.split("-")[0]);
            //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(numerominimo_sim));
            Integer numeromaximo_sim = Integer.valueOf(intervalosimulacao.split("-")[1]);
            //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(numeromaximo_sim));
            
            Integer numero_de_simulacoes = numeromaximo_sim - numerominimo_sim + 1;
            
            //rodar o algoritmo de simulacao varias vezes
            for (int i = 0; i < numero_de_simulacoes; i++)
            {
                Integer subsetmax_indice = i + numerominimo_sim;
                
                if (i == 0)
                    jTextAreaOutput.setText("Simulation MULTIPLE/CANDLESSUBSET [0 to " + subsetmax_indice + "]");
                else
                    jTextAreaOutput.append("\n\nSimulation MULTIPLE/CANDLESSUBSET [0 to " + subsetmax_indice + "]");
           
                
                //atualizar o subset de candles utilizado pela simulacao (neste caso o subset eh igual a todas as candles)
                candlessimulacao = candlessample.subList(0, subsetmax_indice);

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
                }
                
                      String primeiro_ts = retornartimestampcsv(candlessimulacao.get(0).timestampdate);
            String ultimo_ts = retornartimestampcsv(candlessimulacao.get(candlessimulacao.size()-1).timestampdate);
            String ultimo_close = String.valueOf(candlessimulacao.get(candlessimulacao.size()-1).closed);
            String ultimo_bid = String.valueOf(otrader.melhorbid);
            String ultimo_ask = String.valueOf(otrader.melhorask);
            String traderbot_move = (String)mcbctraderbot.respostatradermove_lastrun; 
            String traderbot_supportamount = String.valueOf(((double[]) mcbctraderbot.respostaquantidadesuporte_lastrun)[0]);
            String postrade_baseamount = String.valueOf(otrader.quantidademoedabase);
            String postrade_quoteamount = String.valueOf(otrader.quantidademoedacotacao);
            String postrade_total = String.valueOf(otrader.totalfundos_moedacotacao());
            csvSave = csvSave + "\n" +
                primeiro_ts + ";" + ultimo_ts + ";" + ultimo_close + ";" + ultimo_bid + ";" + ultimo_ask + ";" + traderbot_move + ";" +
                    traderbot_supportamount + ";" + postrade_baseamount + ";" + postrade_quoteamount + ";" + postrade_total + ";" + ultimo_logtrade;    }
        }
        
        if (exportarcsv == true)
        {
            try
            {
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
        jButtonTestRun = new javax.swing.JButton();
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
        jButtonTestRunExportcsv = new javax.swing.JButton();
        jLabelSimulationInterval = new javax.swing.JLabel();
        jTextFieldSimulationInterval = new javax.swing.JTextField();
        jLabelCandlesDataSize = new javax.swing.JLabel();
        jCheckBoxAutoTrade = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bearcode Trader Bot Editor");

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

        jButtonTestRun.setForeground(new java.awt.Color(0, 0, 255));
        jButtonTestRun.setText("Run");
        jButtonTestRun.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonTestRunActionPerformed(evt);
            }
        });

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

        jButtonTestRunExportcsv.setForeground(new java.awt.Color(0, 0, 255));
        jButtonTestRunExportcsv.setText("Run and Export Results");
        jButtonTestRunExportcsv.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonTestRunExportcsvActionPerformed(evt);
            }
        });

        jLabelSimulationInterval.setForeground(new java.awt.Color(255, 255, 255));
        jLabelSimulationInterval.setText("Simulation Interval:");

        jTextFieldSimulationInterval.setBackground(new java.awt.Color(0, 0, 0));
        jTextFieldSimulationInterval.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldSimulationInterval.setText("once|all");
        jTextFieldSimulationInterval.setCaretColor(new java.awt.Color(125, 125, 125));

        jLabelCandlesDataSize.setForeground(new java.awt.Color(255, 255, 255));
        jLabelCandlesDataSize.setText("Candles Data Size:");

        jCheckBoxAutoTrade.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBoxAutoTrade.setSelected(true);
        jCheckBoxAutoTrade.setText("Auto Trade");

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
                        .addGap(0, 52, Short.MAX_VALUE)
                        .addComponent(jLabelCandlesDataSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelBuyFee)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldBuyFee, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabelSellFee)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldSellFee, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelBaseAmount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldBaseAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelQuoteAmount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldQuoteAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelSimulationInterval)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldSimulationInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxAutoTrade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTestRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonTestRunExportcsv)))
                .addContainerGap())
        );
        jPanelPaiLayout.setVerticalGroup(
            jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelScript)
                    .addComponent(jLabelCaretPosition))
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPaiLayout.createSequentialGroup()
                        .addGap(416, 416, 416)
                        .addComponent(jLabelTestParameters3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE))
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
                    .addComponent(jLabelQuoteAmount)
                    .addComponent(jLabelCandlesDataSize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTestRun)
                    .addComponent(jLabelSimulationInterval)
                    .addComponent(jButtonTestRunExportcsv)
                    .addComponent(jTextFieldSimulationInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxAutoTrade))
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

    private void jButtonTestRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestRunActionPerformed
        rodarsimulacao(false);
        atualizarinformacoes_offlinetrader_para_editor();
    }//GEN-LAST:event_jButtonTestRunActionPerformed

    private void jTextFieldBuyFeeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTextFieldBuyFeeActionPerformed
    {//GEN-HEADEREND:event_jTextFieldBuyFeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBuyFeeActionPerformed

    private void jButtonTestRunExportcsvActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonTestRunExportcsvActionPerformed
    {//GEN-HEADEREND:event_jButtonTestRunExportcsvActionPerformed
        rodarsimulacao(true);
        atualizarinformacoes_offlinetrader_para_editor();
    }//GEN-LAST:event_jButtonTestRunExportcsvActionPerformed

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
            java.util.logging.Logger.getLogger(editorbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(editorbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(editorbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(editorbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                new editorbearcodetraderbot(telappai).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLoadFile;
    private javax.swing.JButton jButtonResetEditor;
    private javax.swing.JButton jButtonSaveFile;
    private javax.swing.JButton jButtonTestRun;
    private javax.swing.JButton jButtonTestRunExportcsv;
    private javax.swing.JCheckBox jCheckBoxAutoTrade;
    private javax.swing.JLabel jLabelBaseAmount;
    private javax.swing.JLabel jLabelBuyFee;
    private javax.swing.JLabel jLabelCandlesDataSize;
    private javax.swing.JLabel jLabelCaretPosition;
    private javax.swing.JLabel jLabelCurrentFile;
    private javax.swing.JLabel jLabelOutput;
    private javax.swing.JLabel jLabelParameters;
    private javax.swing.JLabel jLabelQuoteAmount;
    private javax.swing.JLabel jLabelScript;
    private javax.swing.JLabel jLabelSellFee;
    private javax.swing.JLabel jLabelSimulationInterval;
    private javax.swing.JLabel jLabelTestParameters3;
    private javax.swing.JPanel jPanelPai;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaOutput;
    private javax.swing.JTextArea jTextAreaScript;
    private javax.swing.JTextField jTextFieldBaseAmount;
    private javax.swing.JTextField jTextFieldBuyFee;
    private javax.swing.JTextField jTextFieldParameters;
    private javax.swing.JTextField jTextFieldQuoteAmount;
    private javax.swing.JTextField jTextFieldSellFee;
    private javax.swing.JTextField jTextFieldSimulationInterval;
    // End of variables declaration//GEN-END:variables
}