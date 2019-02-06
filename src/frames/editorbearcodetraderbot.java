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
    mierclasses.mcbctradingbotinterpreter mcbctraderbot;
    
    //candles de teste utilizadas pelo editor
    java.util.List<mierclasses.mccandle> candlessample;
    
    //quantidade de moedas base de exemplo
    double quantidadebasesample;
    //quantidade de moedas quote de exemplo
    double quantidadequotesample;
    
    //bid de exemplo
    double bidsample;
    //as de exemplo
    double asksample;
    
    //fees de compra e venda de exemplo
    double feecomprasample;
    double feevendasample;
    
    //handler para o output do jtextareaoutput
    mierclasses.mcjtextareahandler mcjtah;
    
    public editorbearcodetraderbot(main.TelaPrincipal tppai) 
    {
        initComponents();
        
        telappai = tppai;
        mcbctraderbot = new mierclasses.mcbctradingbotinterpreter("testbctraderbot", "Teste Trader Bot", "", "");
        
        candlessample = tppai.msapicomms.receberstockchartsample();
        
        mcjtah = new mierclasses.mcjtextareahandler(jTextAreaOutput);
        
        resetarscripteditor();
    }
    
    void resetarscripteditor()
    {
        //funcao para resetar script editor
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
                "var tradermove = \"buybase\"; //don't do anything\n" +
                "var amountbase = [60]; //buy 60 base currency\n" +
                "var supportamount = Java.to(amountbase,\"double[]\");";
                
        jTextAreaScript.setText(scriptdefault);
        jTextAreaScript.setCaretPosition(0);
        jTextAreaOutput.setText("");
        jTextAreaOutput.setCaretPosition(0);
        jLabelCurrentFile.setText("Current File: (new)");
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
    
    void reconfigurarbearcodeinterpretererodar()
    {
        jTextAreaOutput.setText("");
        
        java.util.List<Double> bidask = telappai.msapicomms.receberlastbidaskofflinetradingsample();
        bidsample = bidask.get(0);
        asksample = bidask.get(1);
        quantidadebasesample = Double.valueOf(jTextFieldBaseAmount.getText());
        quantidadequotesample = Double.valueOf(jTextFieldQuoteAmount.getText());
        feecomprasample = Double.valueOf(jTextFieldBuyFee.getText());
        feevendasample = Double.valueOf(jTextFieldSellFee.getText());
        
        //funcao para repopular 
        //mbcodeinterpreter = new mierclasses.mcbcindicatorinterpreter(idbci, nomebci, conteudoscriptbci, parametrosbearcode,this);
        mcbctraderbot.atualizarscriptparametros(jTextAreaScript.getText(), jTextFieldParameters.getText());
        //String result = mcbctraderbot.rodarscript(candlessample,true,mcjtah);
        String result = mcbctraderbot.rodarscript
        (
                candlessample,
                quantidadebasesample, 
                quantidadequotesample, 
                bidsample,
                asksample,
                feecomprasample,
                feevendasample,
                true,
                mcjtah
        );
        
        if (result.equals("ok"))
        {
            mcjtah.print("\n======\nOK");
        }
        else
        {
            mcjtah.print("\n======\n" + "Exception: " + result);
        }
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
        jTextFieldBaseAmount.setText("1000");
        jTextFieldBaseAmount.setCaretColor(new java.awt.Color(125, 125, 125));

        jLabelBaseAmount.setForeground(new java.awt.Color(255, 255, 255));
        jLabelBaseAmount.setText("Current Base Amount:");

        jLabelQuoteAmount.setForeground(new java.awt.Color(255, 255, 255));
        jLabelQuoteAmount.setText("Current Quote Amount:");

        jTextFieldQuoteAmount.setBackground(new java.awt.Color(0, 0, 0));
        jTextFieldQuoteAmount.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldQuoteAmount.setText("15550");
        jTextFieldQuoteAmount.setCaretColor(new java.awt.Color(125, 125, 125));

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
                    .addGroup(jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jLabelOutput)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTestParameters3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jLabelParameters)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldParameters))
                    .addGroup(jPanelPaiLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 231, Short.MAX_VALUE)
                        .addComponent(jLabelBuyFee)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldBuyFee, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                        .addComponent(jTextFieldQuoteAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonTestRun, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jButtonTestRun)
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
        resetarscripteditor();
    }//GEN-LAST:event_jButtonResetEditorActionPerformed

    private void jButtonLoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoadFileActionPerformed
        carregararquivobcodeedicao();
    }//GEN-LAST:event_jButtonLoadFileActionPerformed

    private void jButtonSaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveFileActionPerformed
        salvararquivobcodeedicao();
    }//GEN-LAST:event_jButtonSaveFileActionPerformed

    private void jButtonTestRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestRunActionPerformed
        reconfigurarbearcodeinterpretererodar();
    }//GEN-LAST:event_jButtonTestRunActionPerformed

    private void jTextFieldBuyFeeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTextFieldBuyFeeActionPerformed
    {//GEN-HEADEREND:event_jTextFieldBuyFeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBuyFeeActionPerformed

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
    private javax.swing.JLabel jLabelBaseAmount;
    private javax.swing.JLabel jLabelBuyFee;
    private javax.swing.JLabel jLabelCaretPosition;
    private javax.swing.JLabel jLabelCurrentFile;
    private javax.swing.JLabel jLabelOutput;
    private javax.swing.JLabel jLabelParameters;
    private javax.swing.JLabel jLabelQuoteAmount;
    private javax.swing.JLabel jLabelScript;
    private javax.swing.JLabel jLabelSellFee;
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
    // End of variables declaration//GEN-END:variables
}