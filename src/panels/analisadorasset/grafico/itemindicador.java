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
public class itemindicador extends javax.swing.JPanel
{
    //jpanel que representa um indicador em uso pelo submodulo grafico em questao
    
    //submodulo grafico ao qual este item indicador pertence
    public panels.analisadorasset.grafico.submodulografico submodulografico;
    
    //classe interpretadora de bearcode (contem o codigo relacionado a este indicador)
    public mierclasses.mcbcindicatorinterpreter mcbcindicador;

    //grafico separado deste item indicador
    public frames.analisadorasset.grafico.chartseparado mfcs;
    
    //variavel que diz se o grafico separado deste indicador esta em bottom
    public Boolean chartseparadoembottom;
    
    //id deste indicador
    public String id;
    
    //construtor novo indicador
    public itemindicador(panels.analisadorasset.grafico.submodulografico mpsmg, String idbearcode, String parametrosbearcode)
    {
        initComponents();
        
        id = "indicator"+java.util.UUID.randomUUID().toString();
        jLabelAbrirJanelaMostrarGraficoSeparado.setVisible(false);
        
        submodulografico = mpsmg;
        
        popularbearcodeindicador(idbearcode,parametrosbearcode);

    }
    
    //construtor recarregar indicador
    public itemindicador(panels.analisadorasset.grafico.submodulografico mpsmg, String idind, String nome, String idbearcode, String parametrosbearcode)
    {
        initComponents();
        
        id = idind;
        jLabelAbrirJanelaMostrarGraficoSeparado.setVisible(false);
        
        jLabelNomeItemIndicador.setText(nome);
        
        submodulografico = mpsmg;
        
        popularbearcodeindicador(idbearcode,parametrosbearcode);

    }
    
     public void renomearitem(String novonome)
    {
        jLabelNomeItemIndicador.setText(novonome);
    }
     
    public String rodarscriptindicador()
    {
        //funcao para rodar script relacionado a este item indicador
        String statusrun = mcbcindicador.rodarscript(submodulografico.mcg.candlesatual,false,null);
        
        return statusrun;
    }

    public void criargraficoseparadoindicador()
    {
        //funcao para criar chartseparado com grafico separado deste indicador, e associa-lo a este
        //mpitemindicador, para poder ser acessado ao clicar no botao G
        mfcs = new frames.analisadorasset.grafico.chartseparado();
        chartseparadoembottom = false;
        jLabelAbrirJanelaMostrarGraficoSeparado.setVisible(true);
    }
    
    public void recarregargraficoseparadoindicador()
    {
        /*
            novoindicador.mcbcindicador.pontosx_lastrun,
            novoindicador.mcbcindicador.pontosy_lastrun,
            novoindicador.mcbcindicador.tituloscript_lastrun,
            novoindicador.mcbcindicador.tipodesenho_lastrun
        */
        mfcs.associarplotchartpaneljanela
        (mfcs.retornarnovoplot_indicador(mcbcindicador.pontosx_lastrun, mcbcindicador.pontosy_lastrun, mcbcindicador.tituloscript_lastrun, mcbcindicador.tipoplot_lastrun)
        );
        
    }
    
    void popularbearcodeindicador(String idbearcode, String parametrosbearcode)
    {
        //public mcbcindicatorinterpreter(String tipo, String id, String nome, String codbcjs, String paramsbcjs)
        //funcao para criar um novo mcbcindicatorinterpreter, que sera utilizado para
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
            String cindicconfig = rootjar + "/outfiles/bearcode/indicators/indicators.mfxconfig";
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
                        caminhoarquivobci = rootjar + "/outfiles/bearcode/indicators/" + arquivobcode;
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
        
        
        mcbcindicador = new mierclasses.mcbcindicatorinterpreter(idbci, nomebci, conteudoscriptbci, parametrosbearcode);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mcbcindicador.tipobcode: " + mcbcindicador.tipobcode);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mcbcindicador.idbcode: " + mcbcindicador.idbcode);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mcbcindicador.nomebcode: " + mcbcindicador.nomebcode);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mcbcindicador.parametrosbcodejs: " + mcbcindicador.parametrosbcodejs);
        //mierclasses.mcfuncoeshelper.mostrarmensagem("mcbcindicador.codigobcodejs: " + mcbcindicador.codigobcodejs);
    }
    
    
    //funcoes helper
    double retornarpontoy_dadotimestamp(java.util.Date timestamppontoy)
    {
        double[] yvalues_double = (double[]) mcbcindicador.pontosy_lastrun;
        java.util.Date[] xvalues_date = (java.util.Date[]) mcbcindicador.pontosx_lastrun;
        
        for (int i = 0; i < xvalues_date.length; i++)
        {
            if (xvalues_date[i] == timestamppontoy)
            {
                return yvalues_double[i];
            }
        }
        return -30041993; //valor especifico double retornado caso nenhum valor seja encontrado
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
        jLabelEscolherGraficoParaBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/upgray.png"))); // NOI18N
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
                .addComponent(jLabelNomeItemIndicador, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelEscolherGraficoParaBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(jLabelEscolherGraficoParaBottom, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelNomeItemIndicador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        frames.analisadorasset.grafico.renomearitemindicador mfrii = new frames.analisadorasset.grafico.renomearitemindicador(this);
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
        if (chartseparadoembottom == false)
        {
            submodulografico.setarGraficoIndicadorSecundario(this);
        }
        else if (chartseparadoembottom == true)
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
