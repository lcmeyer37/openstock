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

/**
 *
 * @author lucasmeyer
 */
public class itemanotacao extends javax.swing.JPanel
{
    //item utilizado para mostrar anotacoes introduzidas no grafico
    
    //submodulo grafico ao qual este item anotacao pertence
    public panels.analisadorasset.grafico.submodulografico submodulografico;
    
    //variavel que diz que tipo de anotacao eh esta, ex: linha, fibonacci, etc.
    public String tipoanotacao;
    
    //jfree xyannotation list, pertencente a este item anotacao
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> anotacao;
    
    //id desta anotacao (utilizado para delecao)
    public String id;
    
    //construtor nova anotacao
    public itemanotacao(panels.analisadorasset.grafico.submodulografico smg, String tipo, java.util.List<org.jfree.chart.annotations.XYAnnotation> subans)
    {
        initComponents();
        
        id = "annotation"+java.util.UUID.randomUUID().toString();
        
        submodulografico = smg;
        tipoanotacao = tipo;
        anotacao = subans;
    }
    
    //construtor recarregar anotacao
    public itemanotacao(panels.analisadorasset.grafico.submodulografico smg, String nome, String idan, String tipo, java.util.List<org.jfree.chart.annotations.XYAnnotation> subans)
    {
        initComponents();
        
        id = idan;
        
        jLabelNomeItemAnotacao.setText(nome);
        
        submodulografico = smg;
        tipoanotacao = tipo;
        anotacao = subans;
    }
   
    
    public void renomearitem(String novonome)
    {
        if (tipoanotacao.equals("text"))
        {
            //caso seja anotacao, tambem renomear a anotacao no grafico em si
            org.jfree.chart.annotations.XYTextAnnotation antexto = (org.jfree.chart.annotations.XYTextAnnotation)anotacao.get(0);
            antexto.setText(novonome);
        }
        jLabelNomeItemAnotacao.setText(novonome);
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
        jLabelNomeItemAnotacao = new javax.swing.JLabel();
        jLabelRemoverAnotacao = new javax.swing.JLabel();
        jLabelAbrirJanelaRenomearItemAnotacao = new javax.swing.JLabel();

        setBackground(new java.awt.Color(155, 155, 155));

        jPanelSub.setBackground(new java.awt.Color(55, 55, 55));

        jLabelNomeItemAnotacao.setForeground(new java.awt.Color(255, 255, 255));
        jLabelNomeItemAnotacao.setText("New Annotation");

        jLabelRemoverAnotacao.setForeground(new java.awt.Color(255, 0, 0));
        jLabelRemoverAnotacao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRemoverAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/trashred.png"))); // NOI18N
        jLabelRemoverAnotacao.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelRemoverAnotacaoMouseClicked(evt);
            }
        });

        jLabelAbrirJanelaRenomearItemAnotacao.setForeground(new java.awt.Color(255, 255, 125));
        jLabelAbrirJanelaRenomearItemAnotacao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAbrirJanelaRenomearItemAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyellow.png"))); // NOI18N
        jLabelAbrirJanelaRenomearItemAnotacao.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelAbrirJanelaRenomearItemAnotacaoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanelSubLayout = new javax.swing.GroupLayout(jPanelSub);
        jPanelSub.setLayout(jPanelSubLayout);
        jPanelSubLayout.setHorizontalGroup(
            jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSubLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelNomeItemAnotacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelAbrirJanelaRenomearItemAnotacao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelRemoverAnotacao)
                .addContainerGap())
        );
        jPanelSubLayout.setVerticalGroup(
            jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSubLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelNomeItemAnotacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabelAbrirJanelaRenomearItemAnotacao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelRemoverAnotacao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
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

    private void jLabelAbrirJanelaRenomearItemAnotacaoMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelAbrirJanelaRenomearItemAnotacaoMouseClicked
    {//GEN-HEADEREND:event_jLabelAbrirJanelaRenomearItemAnotacaoMouseClicked
        frames.analisadorasset.grafico.renomearitemanotacao mfrig = new frames.analisadorasset.grafico.renomearitemanotacao(this);
        mfrig.show();
    }//GEN-LAST:event_jLabelAbrirJanelaRenomearItemAnotacaoMouseClicked

    private void jLabelRemoverAnotacaoMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelRemoverAnotacaoMouseClicked
    {//GEN-HEADEREND:event_jLabelRemoverAnotacaoMouseClicked
        submodulografico.removerAnotacao(this);
    }//GEN-LAST:event_jLabelRemoverAnotacaoMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelAbrirJanelaRenomearItemAnotacao;
    public javax.swing.JLabel jLabelNomeItemAnotacao;
    private javax.swing.JLabel jLabelRemoverAnotacao;
    private javax.swing.JPanel jPanelSub;
    // End of variables declaration//GEN-END:variables
}
