/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierframes;


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
public class mfadicionarindicador extends javax.swing.JFrame
{
    public static mierpanels.mpsubmodulografico submodulopai;
    
    /**
     * Creates new form mfadicionarindicador
     */
    public mfadicionarindicador(mierpanels.mpsubmodulografico smodpai)
    {
        initComponents();
        
        submodulopai = smodpai;
        
        inicializarjanelaadicionarindicador();
    }
    
    void inicializarjanelaadicionarindicador()
    {
        //abrir documento /arquivosconfig/indicadores.mfxconfig para ler indicadores disponiveis para uso
        //e criar itens com indicadores disponiveis no jComboBoxIndicadoresDisponiveis
        
        try
        {
            jComboBoxIndicadoresDisponiveis.removeAllItems();
            
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
                    String nome = elIndicador.getElementsByTagName("Nome").item(0).getTextContent();
                    String caminhobcode = elIndicador.getElementsByTagName("ArquivoBearcode").item(0).getTextContent();
                    
                    jComboBoxIndicadoresDisponiveis.addItem(nome + " [" + id + "]");
                }
            }
        }
        catch (Exception e) 
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("Uma exceção ocorreu: " + e.toString());
        }
    }
    
    void adicionarindicador()
    {
        String indicadorselecionadostring = (String)jComboBoxIndicadoresDisponiveis.getSelectedItem();
        //mierclasses.mcfuncoeshelper.mostrarmensagem(indicadorselecionadostring);
        String idbc = (indicadorselecionadostring.split("\\[")[1]).split("\\]")[0];
        //mierclasses.mcfuncoeshelper.mostrarmensagem(idbc);
        String parametrosbc = jTextFieldParametrosIndicador.getText();
        //mierclasses.mcfuncoeshelper.mostrarmensagem(parametrosbc);
        submodulopai.adicionarIndicador(idbc, parametrosbc);
        this.dispose();
    }
    
    
    /*
    LER XML EM JAVA - EXEMPLO 
    
    
    <?xml version="1.0"?>
<company>
	<staff id="1001">
		<firstname>yong</firstname>
		<lastname>mook kim</lastname>
		<nickname>mkyong</nickname>
		<salary>100000</salary>
	</staff>
	<staff id="2001">
		<firstname>low</firstname>
		<lastname>yin fong</lastname>
		<nickname>fong fong</nickname>
		<salary>200000</salary>
	</staff>
</company>
    
    package com.mkyong.seo;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class ReadXMLFile {

  public static void main(String argv[]) {

    try {

	File fXmlFile = new File("/Users/mkyong/staff.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
			
	//optional, but recommended
	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	doc.getDocumentElement().normalize();

	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
	NodeList nList = doc.getElementsByTagName("staff");
			
	System.out.println("----------------------------");

	for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);
				
		System.out.println("\nCurrent Element :" + nNode.getNodeName());
				
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;

			System.out.println("Staff id : " + eElement.getAttribute("id"));
			System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
			System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
			System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
			System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

		}
	}
    } catch (Exception e) {
	e.printStackTrace();
    }
  }

}

    */

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabelNome = new javax.swing.JLabel();
        jComboBoxIndicadoresDisponiveis = new javax.swing.JComboBox<>();
        jLabelParametros = new javax.swing.JLabel();
        jTextFieldParametrosIndicador = new javax.swing.JTextField();
        jButtonAdicionarIndicador = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add New Indicator");

        jLabelNome.setText("Name:");

        jComboBoxIndicadoresDisponiveis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(no indicators)" }));

        jLabelParametros.setText("Parameters:");

        jButtonAdicionarIndicador.setText("Add");
        jButtonAdicionarIndicador.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAdicionarIndicadorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxIndicadoresDisponiveis, 0, 342, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelParametros)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldParametrosIndicador))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonAdicionarIndicador)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelNome)
                    .addComponent(jComboBoxIndicadoresDisponiveis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelParametros)
                    .addComponent(jTextFieldParametrosIndicador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonAdicionarIndicador)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAdicionarIndicadorActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAdicionarIndicadorActionPerformed
    {//GEN-HEADEREND:event_jButtonAdicionarIndicadorActionPerformed
        adicionarindicador();
    }//GEN-LAST:event_jButtonAdicionarIndicadorActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(mfadicionarindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(mfadicionarindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(mfadicionarindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(mfadicionarindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new mfadicionarindicador(submodulopai).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdicionarIndicador;
    private javax.swing.JComboBox<String> jComboBoxIndicadoresDisponiveis;
    private javax.swing.JLabel jLabelNome;
    private javax.swing.JLabel jLabelParametros;
    private javax.swing.JTextField jTextFieldParametrosIndicador;
    // End of variables declaration//GEN-END:variables
}


