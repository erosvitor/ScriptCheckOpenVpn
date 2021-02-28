
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckOpenVpn extends Thread {

  private SimpleDateFormat brazilDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
  private String ipToCheck;

  public CheckOpenVpn(String ipToCheck) {
    this.ipToCheck = ipToCheck;
  }

  @Override
  public void run() {
    while (!isInterrupted()) {
      if (vpnIsDown()) {
        System.out.println(String.format("%s: VPN disconnected. Reconnecting...", brazilDateFormat.format(new Date())));
        startVpn();
      } else {
        System.out.println(String.format("%s: VPN connected!", brazilDateFormat.format(new Date())));
      }
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean vpnIsDown() {
    boolean isDown = false;
    Process resultPing = null;
    InputStreamReader isr = null;
    BufferedReader br = null;
    try {
      resultPing = Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "ping " + ipToCheck + " -c 1 | grep 'Time to live exceeded'"});
      isr = new InputStreamReader(resultPing.getInputStream());
      br = new BufferedReader(isr);
      String line = null;
      while ((line = br.readLine()) != null) {
        if (line.contains("Time to live exceeded")) {
          isDown = true;
          break;
        }
      }
    } catch (IOException e) {
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
        }
      }
      if (isr != null) {
        try {
          isr.close();
        } catch (IOException e) {
        }
      }
    }
    return isDown;
  }

  private void startVpn() {
    try {
      Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "sudo openvpn --config settings.ovpn"});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {

    if (args.length != 1) {
      System.out.println("CheckOpenVpn");
      System.out.println("");
      System.out.println("  Usage: java -jar CheckOpenVpn <ip-to-check>");
      System.out.println("\n");
    } else {
      String ipToCheck = args[0];
      new CheckOpenVpn(ipToCheck).start();
    }

  }

}
