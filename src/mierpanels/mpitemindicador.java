/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierpanels;

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
public class mpitemindicador extends javax.swing.JPanel
{
    //jpanel que representa um indicador em uso pelo submodulo grafico em questao
    
    //submodulo grafico ao qual este item indicador pertence
    public mierpanels.mpsubmodulografico submodulografico;
    
    //classe interpretadora de bearcode (contem o codigo relacionado a este indicador)
    public mierclasses.mcbearcodeinterpreter mbcodeinterpreter;

    //grafico separado deste item indicador
    public mierframes.mfchartseparado mfcs;
    
    //id deste indicador
    String id;
    
    //construtor novo indicador
    public mpitemindicador(mierpanels.mpsubmodulografico mpsmg, String idbearcode, String parametrosbearcode)
    {
        initComponents();
        
        id = "indicator"+java.util.UUID.randomUUID().toString();
        jLabelAbrirJanelaMostrarGraficoSeparado.setVisible(false);
        
        submodulografico = mpsmg;
        
        criarcodeengine(idbearcode,parametrosbearcode);

    }
    
    //construtor recarregar indicador
    public mpitemindicador(mierpanels.mpsubmodulografico mpsmg, String idind, String nome, String idbearcode, String parametrosbearcode)
    {
        initComponents();
        
        id = idind;
        jLabelAbrirJanelaMostrarGraficoSeparado.setVisible(false);
        
        jLabelNomeItemIndicador.setText(nome);
        
        submodulografico = mpsmg;
        
        criarcodeengine(idbearcode,parametrosbearcode);

    }
    
     public void renomearitem(String novonome)
    {
        jLabelNomeItemIndicador.setText(novonome);
    }
     
    public String rodarscriptindicador()
    {
        //funcao para rodar script relacionado a este item indicador
        String statusrun = mbcodeinterpreter.rodarscript(submodulografico.mcg.candlesatual,false,null);
        
        return statusrun;
    }

    public void criargraficoseparadoindicador()
    {
        //funcao para criar mfchartseparado com grafico separado deste indicador, e associa-lo a este
        //mpitemindicador, para poder ser acessado ao clicar no botao G
        mfcs = new mierframes.mfchartseparado();
        jLabelAbrirJanelaMostrarGraficoSeparado.setVisible(true);
    }
    
    public void recarregargraficoseparadoindicador()
    {
        /*
            novoindicador.mbcodeinterpreter.pontosx_lastrun,
            novoindicador.mbcodeinterpreter.pontosy_lastrun,
            novoindicador.mbcodeinterpreter.tituloscript_lastrun,
            novoindicador.mbcodeinterpreter.tipodesenho_lastrun
        */
        mfcs.associarplotchartpaneljanela
        (
                mfcs.retornarnovoplot_indicador(mbcodeinterpreter.pontosx_lastrun, mbcodeinterpreter.pontosy_lastrun, mbcodeinterpreter.tituloscript_lastrun, mbcodeinterpreter.tipoplot_lastrun)
        );
        
    }
    
    void criarcodeengine(String idbearcode, String parametrosbearcode)
    {
        //public mcbearcodeinterpreter(String tipo, String id, String nome, String codbcjs, String paramsbcjs)
        //funcao para criar um novo mcbearcodeinterpreter, que sera utilizado para
        //interpretar o codigo bearcode e gerar os valores x e y relacionados a este indicador
        
        //String tipobci = "indicador";
        //mierclasses.mcfuncoeshelper.mostrarmensagem("tipobci: " + tipobci);
        String idbci = idbearcode;
        //mierclasses.mcfuncoeshelper.mostrarmensagem("idbci: " + idbci);
        String nomebci = "";
        String caminhoarquivobci = "";
        // <editor-fold defaultstate="collapsed" desc="encontrar caminho e nome deste bearcode utilizando o id">
    
        try
        {
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String cindicconfig = rootjar + "/outfiles/bearcode/indicators.mfxconfig";
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cindicconfig);
            
            File xmlArquivo = new File(cindicconfig);
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
        
            Document document = dbuilder.parse(xmlArquivo);
            
            NodeList listaIndicadoresDisponiveis = document.getElementsByTagName("Indicator");
            
            for (int i = 0; i < listaIndicadoresDisponiveis.getLength(); i++)
            {
                Node nodeIndicador = listaIndicadoresDisponiveis.item(i);

                if (nodeIndicador.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element elIndicador = (Element) nodeIndicador;
                    
                    String id =  elIndicador.getElementsByTagName("BCID").item(0).getTextContent();
                    //mierclasses.mcfuncoeshelper.mostrarmensagem("id: " + id);
                    
                    if (id.equals(idbci) == true)
                    {
                        String nome = elIndicador.getElementsByTagName("Name").item(0).getTextContent();
                        nomebci = nome;
                        //mierclasses.mcfuncoeshelper.mostrarmensagem("nome: " + nome);
                        String arquivobcode = elIndicador.getElementsByTagName("BearcodeFile").item(0).getTextContent();
                        //mierclasses.mcfuncoeshelper.mostrarmensagem("arquivobcode: " + arquivobcode);
                        caminhoarquivobci = rootjar + "/outfiles/bearcode/" + arquivobcode;
                        //mierclasses.mcfuncoeshelper.mostrarmensagem("caminhoarquivobci: " + caminhoarquivobci);
                        break;
                    }
                    
                }
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            mierclasses.mcfuncoeshelper.mostrarmensagem("Uma exceção ocorreu: " + e.getLocalizedMessage());
        }
        //</editor-fold>
        
        String conteudoscriptbci = "";
        //passar para conteudoscriptbci o codigo javascript bearcode
        java.util.List<String> lines = java.util.Collections.emptyList(); 
        try
        { 
            lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(caminhoarquivobci), java.nio.charset.StandardCharsets.UTF_8); 
        
            for (int i = 0; i < lines.size(); i++)
            {
                conteudoscriptbci = conteudoscriptbci + "\n" + lines.get(i);
                
            }
            //mierclasses.mcfuncoeshelper.mostrarmensagem("conteudoscriptbci: " + conteudoscriptbci);
        } 
        catch (Exception e) 
        { 
            e.printStackTrace();
            mierclasses.mcfuncoeshelper.mostrarmensagem("Uma exceção ocorreu: " + e.toString());
        } 
        
        
        mbcodeinterpreter = new mierclasses.mcbearcodeinterpreter(idbci, nomebci, conteudoscriptbci, parametrosbearcode);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mbcodeinterpreter.tipobcode: " + mbcodeinterpreter.tipobcode);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mbcodeinterpreter.idbcode: " + mbcodeinterpreter.idbcode);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mbcodeinterpreter.nomebcode: " + mbcodeinterpreter.nomebcode);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mbcodeinterpreter.parametrosbcodejs: " + mbcodeinterpreter.parametrosbcodejs);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mbcodeinterpreter.codigobcodejs: " + mbcodeinterpreter.codigobcodejs);
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

        jPanelSub = new javax.swing.JPanel();
        jLabelNomeItemIndicador = new javax.swing.JLabel();
        jLabeRemoverItemIndicador = new javax.swing.JLabel();
        jLabelAbrirJanelaRenomearItemIndicador = new javax.swing.JLabel();
        jLabelAbrirJanelaMostrarGraficoSeparado = new javax.swing.JLabel();
        jLabelEscolherGraficoParaBottom = new javax.swing.JLabel();

        setBackground(new java.awt.Color(155, 155, 155));

        jPanelSub.setBackground(new java.awt.Color(55, 55, 55));

        jLabelNomeItemIndicador.setForeground(new java.awt.Color(255, 255, 255));
        jLabelNomeItemIndicador.setText("New Indicator");

        jLabeRemoverItemIndicador.setForeground(new java.awt.Color(255, 0, 0));
        jLabeRemoverItemIndicador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabeRemoverItemIndicador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/trashred.png"))); // NOI18N
        jLabeRemoverItemIndicador.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabeRemoverItemIndicadorMouseClicked(evt);
            }
        });

        jLabelAbrirJanelaRenomearItemIndicador.setForeground(new java.awt.Color(255, 255, 125));
        jLabelAbrirJanelaRenomearItemIndicador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAbrirJanelaRenomearItemIndicador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyellow.png"))); // NOI18N
        jLabelAbrirJanelaRenomearItemIndicador.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelAbrirJanelaRenomearItemIndicadorMouseClicked(evt);
            }
        });

        jLabelAbrirJanelaMostrarGraficoSeparado.setForeground(new java.awt.Color(125, 255, 255));
        jLabelAbrirJanelaMostrarGraficoSeparado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAbrirJanelaMostrarGraficoSeparado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/graphblue.png"))); // NOI18N
        jLabelAbrirJanelaMostrarGraficoSeparado.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelAbrirJanelaMostrarGraficoSeparadoMouseClicked(evt);
            }
        });

        jLabelEscolherGraficoParaBottom.setForeground(new java.awt.Color(125, 255, 255));
        jLabelEscolherGraficoParaBottom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelEscolherGraficoParaBottom.setText("N");
        jLabelEscolherGraficoParaBottom.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelEscolherGraficoParaBottomMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanelSubLayout = new javax.swing.GroupLayout(jPanelSub);
        jPanelSub.setLayout(jPanelSubLayout);
        jPanelSubLayout.setHorizontalGroup(
            jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSubLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelNomeItemIndicador, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelEscolherGraficoParaBottom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelAbrirJanelaMostrarGraficoSeparado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelAbrirJanelaRenomearItemIndicador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabeRemoverItemIndicador, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelSubLayout.setVerticalGroup(
            jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSubLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabelAbrirJanelaRenomearItemIndicador, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabeRemoverItemIndicador, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelAbrirJanelaMostrarGraficoSeparado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelEscolherGraficoParaBottom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelNomeItemIndicador, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelSub, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelSub, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabelAbrirJanelaRenomearItemIndicadorMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelAbrirJanelaRenomearItemIndicadorMouseClicked
    {//GEN-HEADEREND:event_jLabelAbrirJanelaRenomearItemIndicadorMouseClicked
        mierframes.mfrenomearitemindicador mfrii = new mierframes.mfrenomearitemindicador(this);
        mfrii.show();
    }//GEN-LAST:event_jLabelAbrirJanelaRenomearItemIndicadorMouseClicked

    private void jLabeRemoverItemIndicadorMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabeRemoverItemIndicadorMouseClicked
    {//GEN-HEADEREND:event_jLabeRemoverItemIndicadorMouseClicked
        submodulografico.removerIndicador(this);
    }//GEN-LAST:event_jLabeRemoverItemIndicadorMouseClicked

    private void jLabelAbrirJanelaMostrarGraficoSeparadoMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelAbrirJanelaMostrarGraficoSeparadoMouseClicked
    {//GEN-HEADEREND:event_jLabelAbrirJanelaMostrarGraficoSeparadoMouseClicked
        mfcs.show();
    }//GEN-LAST:event_jLabelAbrirJanelaMostrarGraficoSeparadoMouseClicked

    private void jLabelEscolherGraficoParaBottomMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelEscolherGraficoParaBottomMouseClicked
    {//GEN-HEADEREND:event_jLabelEscolherGraficoParaBottomMouseClicked
        if (jLabelEscolherGraficoParaBottom.getText().equals("N"))
        {
            submodulografico.setarGraficoIndicadorSecundario(this);
        }
        else if (jLabelEscolherGraficoParaBottom.getText().equals("S"))
        {
            submodulografico.removerGraficoIndicadorSecundario();
        }
    }//GEN-LAST:event_jLabelEscolherGraficoParaBottomMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabeRemoverItemIndicador;
    private javax.swing.JLabel jLabelAbrirJanelaMostrarGraficoSeparado;
    private javax.swing.JLabel jLabelAbrirJanelaRenomearItemIndicador;
    public javax.swing.JLabel jLabelEscolherGraficoParaBottom;
    public javax.swing.JLabel jLabelNomeItemIndicador;
    private javax.swing.JPanel jPanelSub;
    // End of variables declaration//GEN-END:variables
}
