/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package networkproj;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Arrays;


/**
 *
 * @author ghade
 */

public class ConRoom extends javax.swing.JFrame {

    private PrintWriter out;
    private BufferedReader in;
    private Server.ServerThread client; // Reference to the server thread

    public ConRoom(Server.ServerThread client) {
        this.client = client; // Initialize with the client reference
        initComponents();
        startListeningForUpdates(); // Start listening for server updates
    }

private void startListeningForUpdates() {
    new Thread(() -> {
        try {
            in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
            
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message); // Debug log
                if (message.startsWith("Connected players:")) {
                    String userListString = message.substring("Connected players: ".length());
                    List<String> userList = Arrays.asList(userListString.split(","));
                    updateUserList(userList); // Update the user list in the UI
                } else {
                    // Optionally, update the text area with other types of message
                    String username = client.getUsername();
                    updateTextArea(username); // New method to update the text area with other messages
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print error for debugging
        }
    }).start();
}

private void updateUserList(List<String> connectedUsers) {
    SwingUtilities.invokeLater(() -> {
        if (connectedUsers.isEmpty()) {
            jTextArea1.setText("No players connected."); // Message if no players are connected
        } else {
            jTextArea1.setText(String.join("\n", connectedUsers)); // Update the text area with connected usernames
        }
    });
}

// Optional: New method to handle general text updates to the JTextArea
private void updateTextArea(String message) {
    SwingUtilities.invokeLater(() -> {
        jTextArea1.append("\n" + message); // Append the message to the text area
    });
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Connected Players");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Join Game");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(72, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(152, 152, 152))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
  try {
            client.sendMessage("play");
            JOptionPane.showMessageDialog(this, "You have entered the waiting room.", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            WaitingRoom waitingRoomFrame = new WaitingRoom( client);
            waitingRoomFrame.setVisible(true);
            this.dispose(); // Close the current frame
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error entering waiting room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    
public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }}

        /* Create and display the form */
        

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
