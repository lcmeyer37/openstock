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

    //id deste indicador
    String id;
    
    /**
     * Creates new form mpsimbolo
     */
    public mpitemindicador(mierpanels.mpsubmodulografico mpsmg, String idbearcode, String parametrosbearcode)
    {
        initComponents();
        
        id = "indicador"+java.util.UUID.randomUUID().toString();
        
        submodulografico = mpsmg;
        
        adicionaritemindicador(idbearcode,parametrosbearcode);
        rodarscriptindicador();
        desenhargraficoindicador();
        
    }
    
     public void renomearitem(String novonome)
    {
        jLabelNomeItemIndicador.setText(novonome);
    }
     
    void rodarscriptindicador()
    {
        //funcao para rodar script relacionado a este item indicador
        mbcodeinterpreter.rodarscript();
    }
    
    void desenhargraficoindicador()
    {
        /*
        public void adicionargraficoindicador
        (
            Object xvalues,
            Object yvalues,
            String tituloscript,
            String descricaoeixoy,
            String localgrafico,
            String tipografico
        )*/
        submodulografico.mcg.adicionarplotohlc_indicador
                (
                        id,
                        mbcodeinterpreter.pontosx_lastrun,
                        mbcodeinterpreter.pontosy_lastrun,
                        mbcodeinterpreter.tituloscript_lastrun,
                        mbcodeinterpreter.tipoeixoy_lastrun,
                        mbcodeinterpreter.tipodesenho_lastrun
                );
    }
     
    void adicionaritemindicador(String idbearcode, String parametrosbearcode)
    {
        //public mcbearcodeinterpreter(String tipo, String id, String nome, String codbcjs, String paramsbcjs)
        //funcao para criar um novo mcbearcodeinterpreter, que sera utilizado para
        //interpretar o codigo bearcode e gerar os valores x e y relacionados a este indicador
        
        String tipobci = "indicador";
        //mierclasses.mcfuncoeshelper.mostrarmensagem("tipobci: " + tipobci);
        String idbci = idbearcode;
        //mierclasses.mcfuncoeshelper.mostrarmensagem("idbci: " + idbci);
        String nomebci = "";
        String caminhoarquivobci = "";
        // <editor-fold defaultstate="collapsed" desc="encontrar caminho e nome deste bearcode utilizando o id">
    
        try
        {
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String cindicconfig = rootjar + "/arquivosconfig/indicadores.mfxconfig";
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cindicconfig);
            
            File xmlArquivo = new File(cindicconfig);
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
        
            Document document = dbuilder.parse(xmlArquivo);
            
            NodeList listaIndicadoresDisponiveis = document.getElementsByTagName("Indicador");
            
            for (int i = 0; i < listaIndicadoresDisponiveis.getLength(); i++)
            {
                Node nodeIndicador = listaIndicadoresDisponiveis.item(i);

                if (nodeIndicador.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element elIndicador = (Element) nodeIndicador;
                    
                    String id =  elIndicador.getElementsByTagName("ID").item(0).getTextContent();
                    //mierclasses.mcfuncoeshelper.mostrarmensagem("id: " + id);
                    
                    if (id.equals(idbci) == true)
                    {
                        String nome = elIndicador.getElementsByTagName("Nome").item(0).getTextContent();
                        nomebci = nome;
                        //mierclasses.mcfuncoeshelper.mostrarmensagem("nome: " + nome);
                        String arquivobcode = elIndicador.getElementsByTagName("ArquivoBearcode").item(0).getTextContent();
                        //mierclasses.mcfuncoeshelper.mostrarmensagem("arquivobcode: " + arquivobcode);
                        caminhoarquivobci = rootjar + "/arquivosconfig/" + arquivobcode;
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
        
        mbcodeinterpreter = new mierclasses.mcbearcodeinterpreter(idbci, nomebci, conteudoscriptbci, parametrosbearcode,this);
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

        setBackground(new java.awt.Color(155, 155, 155));

        jPanelSub.setBackground(new java.awt.Color(55, 55, 55));

        jLabelNomeItemIndicador.setForeground(new java.awt.Color(255, 255, 255));
        jLabelNomeItemIndicador.setText("Novo Indicador");

        jLabeRemoverItemIndicador.setForeground(new java.awt.Color(255, 0, 0));
        jLabeRemoverItemIndicador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabeRemoverItemIndicador.setText(":");
        jLabeRemoverItemIndicador.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabeRemoverItemIndicadorMouseClicked(evt);
            }
        });

        jLabelAbrirJanelaRenomearItemIndicador.setForeground(new java.awt.Color(255, 255, 125));
        jLabelAbrirJanelaRenomearItemIndicador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAbrirJanelaRenomearItemIndicador.setText(":");
        jLabelAbrirJanelaRenomearItemIndicador.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelAbrirJanelaRenomearItemIndicadorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanelSubLayout = new javax.swing.GroupLayout(jPanelSub);
        jPanelSub.setLayout(jPanelSubLayout);
        jPanelSubLayout.setHorizontalGroup(
            jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSubLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelNomeItemIndicador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelAbrirJanelaRenomearItemIndicador, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabeRemoverItemIndicador, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelSubLayout.setVerticalGroup(
            jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSubLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelNomeItemIndicador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabeRemoverItemIndicador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelAbrirJanelaRenomearItemIndicador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabeRemoverItemIndicador;
    private javax.swing.JLabel jLabelAbrirJanelaRenomearItemIndicador;
    public javax.swing.JLabel jLabelNomeItemIndicador;
    private javax.swing.JPanel jPanelSub;
    // End of variables declaration//GEN-END:variables
}
