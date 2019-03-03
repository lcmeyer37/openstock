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
package outrasopcoes.editorbearcodeindicador;

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
public class frame_editorbearcodeindicador extends javax.swing.JFrame 
{

    //janela utilizada para edicao de bearcode scripts
    
    //tela principal pai
    public static main.frame_telaprincipal telappai;
    
    //classe interpretadora de bearcode (contem o codigo relacionado a este indicador)
    mierclasses.mcbcindicatorinterpreter mcbcindicador;
    
    //candles de teste utilizadas pelo editor
    java.util.List<mierclasses.mccandle> candlessample;
    
    //handler para o output do jtextareaoutput
    mierclasses.mcjtextareahandler mcjtah;
    
    //RSyntaxTextArea utilizado pelo editor
    org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rsTextArea;
    
    public frame_editorbearcodeindicador(main.frame_telaprincipal tppai) 
    {
        initComponents();
        
        telappai = tppai;
        mcbcindicador = new mierclasses.mcbcindicatorinterpreter("testbcindicator", "Teste Indicator", "", "");
        candlessample = tppai.msapicomms.offline_receberstockcandlessample();
        mcjtah = new mierclasses.mcjtextareahandler(jTextAreaOutput);
        
        inicializarRSyntaxTextArea();
        resetarscripteditor();
    }
    
    void inicializarRSyntaxTextArea()
    {
        //classe text area com syntax parser para javascript
        rsTextArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea(20, 60);
        rsTextArea.setSyntaxEditingStyle(org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        rsTextArea.setCodeFoldingEnabled(true);
        org.fife.ui.rtextarea.RTextScrollPane sp = new org.fife.ui.rtextarea.RTextScrollPane(rsTextArea);
        jPanelEditorHolder.add(sp);
        jPanelEditorHolder.setLayout(new java.awt.GridLayout(1,1));
        this.validate();
        this.repaint();
    }
    
    void resetarscripteditor()
    {
        //funcao para resetar script editor
        String scriptdefault = "//bearcode indicator sample\n" +
                "runoutput.print(\"sample code\");\n" +
                "\n" +
                "//sample candles for processing (data at /outfiles/samples/apple5y.json)\n" +
                "var candlesinput = candles;\n" +
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
                "//mandatory description for the script\n" +
                "//in the line below there is an example of \"name of script\" indicator, that uses for the\n" +
                "//X-axis timestamps, and that it should be drawn on top of the candles chart as a line\n" +
                "var scriptdescription = \"indicator script title;drawoncandles\";\n" +
                "\n" +
                "//mandatory return values for the script\n" +
                "var yvalues = Java.to(yvalues_indicator,\"double[]\");\n" +
                "var xvalues = Java.to(xvaluestimestamp_indicator,\"java.util.Date[]\");";

        
        rsTextArea.setText(scriptdefault);
        rsTextArea.setCaretPosition(0);
        jTextAreaOutput.setText("");
        jTextAreaOutput.setCaretPosition(0);
    }
    
    String retornarstringposicaocaret()
    {
        try
        {
            int linenum = 1;
            int columnnum = 1;

            int caretpos = rsTextArea.getCaretPosition();
            linenum = rsTextArea.getLineOfOffset(caretpos);

            columnnum = caretpos - rsTextArea.getLineStartOffset(linenum) + 1;

            linenum += 1;
            
            return "(" + linenum + ":" + columnnum + ")";
        }
        catch (Exception ex)
        {
            return "";
        }
    }
    
    void testarscriptindicador()
    {
        jTextAreaOutput.setText("");
        
        //funcao para repopular 
        //mbcodeinterpreter = new mierclasses.mcbcindicatorinterpreter(idbci, nomebci, conteudoscriptbci, parametrosbearcode,this);
        mcbcindicador.atualizarscriptparametros(rsTextArea.getText(), jTextFieldParameters.getText());
        String result = mcbcindicador.rodarscript(candlessample,true,mcjtah);
        
        if (result.equals("ok"))
        {
            mcjtah.print("\n======\nOK");
        }
        else
        {
            mcjtah.print("\n======\n" + "Exception: " + result);
            
            //caso o teste de errado, cancelar funcao
            return;
        }
        
        outrasopcoes.editorbearcodeindicador.frame_resultadosbearcodeindicador frbci = new outrasopcoes.editorbearcodeindicador.frame_resultadosbearcodeindicador(mcbcindicador);
        frbci.show();
    }
    

    void salvararquivobcodeedicao()
    {
        try
        {
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String pastascriptsind = rootjar + "/outfiles/bearcode/indicators";
            fileChooser.setCurrentDirectory(new java.io.File(pastascriptsind));
            javax.swing.filechooser.FileFilter filter = new mierclasses.mcextensionfilefilter("Bearcode File", new String[] { "bearcode" });
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Please choose a location and name for the Open Stock save file");

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) 
            {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave + ".bearcode", "UTF-8");
                writer.println(rsTextArea.getText());
                writer.close();
                
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
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String pastascriptsind = rootjar + "/outfiles/bearcode/indicators";
            fileChooser.setCurrentDirectory(new java.io.File(pastascriptsind));
            javax.swing.filechooser.FileFilter filter = new mierclasses.mcextensionfilefilter("Bearcode File", new String[] { "bearcode" });
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Please choose an Open Stock file to load");

            int userSelection = fileChooser.showOpenDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) 
            {
                
                java.io.File fileToLoad = null;

                try
                {
                    fileToLoad = fileChooser.getSelectedFile();
                    String scripttexto = mierclasses.mcfuncoeshelper.retornarStringArquivo(fileToLoad.getAbsolutePath());
                    
                    rsTextArea.setText(scripttexto);
                    jTextAreaOutput.setText("");
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
        jPanelEditorHolder = new javax.swing.JPanel();
        jLabelScript = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaOutput = new javax.swing.JTextArea();
        jLabelOutput = new javax.swing.JLabel();
        jButtonTestRun = new javax.swing.JButton();
        jButtonSaveFile = new javax.swing.JButton();
        jButtonLoadFile = new javax.swing.JButton();
        jButtonResetEditor = new javax.swing.JButton();
        jLabelTestParameters = new javax.swing.JLabel();
        jTextFieldParameters = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Indicator Editor");

        jPanelPai.setBackground(new java.awt.Color(55, 55, 55));

        javax.swing.GroupLayout jPanelEditorHolderLayout = new javax.swing.GroupLayout(jPanelEditorHolder);
        jPanelEditorHolder.setLayout(jPanelEditorHolderLayout);
        jPanelEditorHolderLayout.setHorizontalGroup(
            jPanelEditorHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelEditorHolderLayout.setVerticalGroup(
            jPanelEditorHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 294, Short.MAX_VALUE)
        );

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

        jButtonResetEditor.setForeground(new java.awt.Color(255, 0, 0));
        jButtonResetEditor.setText("Reset");
        jButtonResetEditor.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonResetEditorActionPerformed(evt);
            }
        });

        jLabelTestParameters.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTestParameters.setText("Test Parameters:");

        jTextFieldParameters.setBackground(new java.awt.Color(0, 0, 0));
        jTextFieldParameters.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldParameters.setCaretColor(new java.awt.Color(125, 125, 125));

        javax.swing.GroupLayout jPanelPaiLayout = new javax.swing.GroupLayout(jPanelPai);
        jPanelPai.setLayout(jPanelPaiLayout);
        jPanelPaiLayout.setHorizontalGroup(
            jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelEditorHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jButtonResetEditor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 425, Short.MAX_VALUE)
                        .addComponent(jButtonLoadFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSaveFile))
                    .addGroup(jPanelPaiLayout.createSequentialGroup()
                        .addComponent(jLabelTestParameters)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldParameters)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonTestRun, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelPaiLayout.createSequentialGroup()
                        .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelScript)
                            .addComponent(jLabelOutput))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelPaiLayout.setVerticalGroup(
            jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelScript)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelEditorHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTestRun)
                    .addComponent(jLabelTestParameters)
                    .addComponent(jTextFieldParameters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jLabelOutput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSaveFile)
                    .addComponent(jButtonLoadFile)
                    .addComponent(jButtonResetEditor))
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
        testarscriptindicador();
    }//GEN-LAST:event_jButtonTestRunActionPerformed

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
            java.util.logging.Logger.getLogger(frame_editorbearcodeindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frame_editorbearcodeindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frame_editorbearcodeindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frame_editorbearcodeindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new frame_editorbearcodeindicador(telappai).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLoadFile;
    private javax.swing.JButton jButtonResetEditor;
    private javax.swing.JButton jButtonSaveFile;
    private javax.swing.JButton jButtonTestRun;
    private javax.swing.JLabel jLabelOutput;
    private javax.swing.JLabel jLabelScript;
    private javax.swing.JLabel jLabelTestParameters;
    private javax.swing.JPanel jPanelEditorHolder;
    private javax.swing.JPanel jPanelPai;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaOutput;
    private javax.swing.JTextField jTextFieldParameters;
    // End of variables declaration//GEN-END:variables
}