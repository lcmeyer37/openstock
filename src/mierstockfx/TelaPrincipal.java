/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierstockfx;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 *
 * @author meyerlu
 */
public class TelaPrincipal extends javax.swing.JFrame 
{

    //novo layout do mierstockfx
    //ele cria varios graficos, e todos tem a sua propria instancia de grafico com anotacoes, indicadores, e pode ser salvo ou carregado
    
    public mierclasses.mcavcomms mav; //classe utilizada para comunicacao com alpha vantage
    
    /**
     * Creates new form TelaPrincipal
     */
    public TelaPrincipal()
    {
        initComponents();
        
        inicializarTelaPrincipal();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Funcionamento Tela Principal">
    
    void inicializarTelaPrincipal()
    {
        //funcao inicial do programa, roda ao ligar
        //limitando para no maximo 20 graficos ao mesmo tempo
        jPanelItensGrafico.setLayout(new java.awt.GridLayout(20,1));
        //holder do grafico eh um grid com um unico item
        jPanelHolderSubmoduloGrafico.setLayout(new java.awt.GridLayout(1,1));
       
        
        //revalidar componentes apos alteracoes graficas e criacoes
        this.validate();
        this.repaint();
        
        //popular mcavcomms, utilizando para comunicar com alpha vantage
        mav = new mierclasses.mcavcomms(resgatarAvKey());
    }
    
    String resgatarAvKey()
    {
        try
        {
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String cgeneralconfig = rootjar + "/configfiles/general.mfxconfig";
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cindicconfig);
            
            File xmlArquivo = new File(cgeneralconfig);
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
        
            Document document = dbuilder.parse(xmlArquivo);
            
            return document.getElementsByTagName("AVKEY").item(0).getTextContent();
        }
        catch (Exception e) 
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("Uma exceção ocorreu: " + e.toString());
            return "";
        }
    }
    
    void adicionarNovoGrafico()
    {
        //existe um limite de 20 graficos por programa
        int numerograficos = jPanelItensGrafico.getComponentCount();
        
        if (numerograficos < 20)
        {
            //funcao para adicionar novo mpitemgrafico no programa, e este mpitemgrafico tem um mpsubmodulografico associado a ele.
            mierpanels.mpitemgrafico novompig = new mierpanels.mpitemgrafico(this);
        
            //adicionar mpitemgrafico ao jPanelItensGrafico
            jPanelItensGrafico.add(novompig);

            //selecionar automaticamente ultimo componente adicionado
            int novonumerograficos = jPanelItensGrafico.getComponentCount();
            selecionarItemGrafico((mierpanels.mpitemgrafico)jPanelItensGrafico.getComponent(novonumerograficos-1));
        }
        else if (numerograficos >= 20)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("Limite de 20 gráficos atingido");
        }
        
        //revalidar componentes apos alteracoes graficas e criacoes
        this.validate();
        this.repaint();
    }
    
    public void removerGrafico(mierpanels.mpitemgrafico mpiadeletar)
    {
        jPanelItensGrafico.remove(mpiadeletar);
        
        int numerograficos = jPanelItensGrafico.getComponentCount();
        if (numerograficos > 0)
        {
            selecionarItemGrafico((mierpanels.mpitemgrafico)jPanelItensGrafico.getComponent(numerograficos-1));
        }
        else
        {
            jPanelHolderSubmoduloGrafico.removeAll();
        }
        
        this.validate();
        this.repaint();
    }
    
    public void selecionarItemGrafico(mierpanels.mpitemgrafico mpigselecionar)
    {
        //funcao para selecionar item grafico
        
        //pintar todos os itens graficos com a cor padrao e esconder os seus submodulos
        int numerograficos = jPanelItensGrafico.getComponentCount();
        for (int i = 0; i < numerograficos; i++)
        {
            mierpanels.mpitemgrafico mpigatual = (mierpanels.mpitemgrafico) jPanelItensGrafico.getComponent(i);
            mpigatual.jPanelSub.setBackground(new java.awt.Color(55, 55, 55));
        }
        jPanelHolderSubmoduloGrafico.removeAll();
        
        //pintar o item grafico desejado com cor de destaque, para demonstrar que este eh o grafico em uso no momento
        mpigselecionar.jPanelSub.setBackground(new java.awt.Color(55, 55, 105));
        
        //mostrar submodulo deste item grafico
        jPanelHolderSubmoduloGrafico.add(mpigselecionar.mpsubgrafico);
        
        
        //revalidar componentes apos alteracoes graficas e criacoes
        this.validate();
        this.repaint();
    }
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

        jPanelHolderTelaPrincipal = new javax.swing.JPanel();
        jPanelHolderSubmoduloGrafico = new javax.swing.JPanel();
        jLabelAdicionarGrafico = new javax.swing.JLabel();
        jButtonJanelaConfiguracoes = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelItensGrafico = new javax.swing.JPanel();
        jButtonAdicionarGrafico = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Open Stock");

        jPanelHolderSubmoduloGrafico.setBackground(new java.awt.Color(55, 55, 55));

        javax.swing.GroupLayout jPanelHolderSubmoduloGraficoLayout = new javax.swing.GroupLayout(jPanelHolderSubmoduloGrafico);
        jPanelHolderSubmoduloGrafico.setLayout(jPanelHolderSubmoduloGraficoLayout);
        jPanelHolderSubmoduloGraficoLayout.setHorizontalGroup(
            jPanelHolderSubmoduloGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 745, Short.MAX_VALUE)
        );
        jPanelHolderSubmoduloGraficoLayout.setVerticalGroup(
            jPanelHolderSubmoduloGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 544, Short.MAX_VALUE)
        );

        jLabelAdicionarGrafico.setText("Assets");

        jButtonJanelaConfiguracoes.setText("Configuration");
        jButtonJanelaConfiguracoes.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonJanelaConfiguracoesActionPerformed(evt);
            }
        });

        jPanelItensGrafico.setBackground(new java.awt.Color(155, 155, 155));

        javax.swing.GroupLayout jPanelItensGraficoLayout = new javax.swing.GroupLayout(jPanelItensGrafico);
        jPanelItensGrafico.setLayout(jPanelItensGraficoLayout);
        jPanelItensGraficoLayout.setHorizontalGroup(
            jPanelItensGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 311, Short.MAX_VALUE)
        );
        jPanelItensGraficoLayout.setVerticalGroup(
            jPanelItensGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 515, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelItensGrafico);

        jButtonAdicionarGrafico.setText("Add");
        jButtonAdicionarGrafico.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAdicionarGraficoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelHolderTelaPrincipalLayout = new javax.swing.GroupLayout(jPanelHolderTelaPrincipal);
        jPanelHolderTelaPrincipal.setLayout(jPanelHolderTelaPrincipalLayout);
        jPanelHolderTelaPrincipalLayout.setHorizontalGroup(
            jPanelHolderTelaPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHolderTelaPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHolderTelaPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanelHolderTelaPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabelAdicionarGrafico)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAdicionarGrafico))
                    .addComponent(jButtonJanelaConfiguracoes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelHolderSubmoduloGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelHolderTelaPrincipalLayout.setVerticalGroup(
            jPanelHolderTelaPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelHolderSubmoduloGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelHolderTelaPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHolderTelaPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelAdicionarGrafico)
                    .addComponent(jButtonAdicionarGrafico))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonJanelaConfiguracoes)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelHolderTelaPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelHolderTelaPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAdicionarGraficoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarGraficoActionPerformed
        adicionarNovoGrafico();
    }//GEN-LAST:event_jButtonAdicionarGraficoActionPerformed

    private void jButtonJanelaConfiguracoesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonJanelaConfiguracoesActionPerformed
    {//GEN-HEADEREND:event_jButtonJanelaConfiguracoesActionPerformed
        mierframes.mfconfiguracoesgerais mfcg = new mierframes.mfconfiguracoesgerais();
        mfcg.show();
    }//GEN-LAST:event_jButtonJanelaConfiguracoesActionPerformed

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
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdicionarGrafico;
    private javax.swing.JButton jButtonJanelaConfiguracoes;
    private javax.swing.JLabel jLabelAdicionarGrafico;
    private javax.swing.JPanel jPanelHolderSubmoduloGrafico;
    private javax.swing.JPanel jPanelHolderTelaPrincipal;
    private javax.swing.JPanel jPanelItensGrafico;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
