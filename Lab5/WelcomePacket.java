import java.net.SocketAddress;

public class WelcomePacket {
    private byte version;
    private byte authMethodsNum;
    private byte[] authMethods;

    public WelcomePacket(byte[] msg) {
        if (msg[0] == 4) {
            version = msg[0];
            return;
        }
        version = msg[0];
        authMethodsNum = msg[1];
        //System.out.println(authMethodsNum);
        authMethods = new byte[authMethodsNum];
        for (int i = 2; i < authMethodsNum + 2; i++) {
            authMethods[i-2] = msg[i];
        }
    }

    public void print() {
        System.out.println("\nHello! version: "+version+" methodsNum: "+authMethodsNum);
        for (int i = 0; i < authMethodsNum; i++) {
            System.out.println("method: "+authMethods[i]);
        }
    }

    public byte[] createAnswer() {
        if (version == 4) {
            return createAnswerToSocks4();
        } else {
            var ansAck = new byte[2];
            ansAck[0] = version;
            ansAck[1] = authMethods[0];
            //System.out.println(ansAck);
            return ansAck;
        }
    }

    public byte[] createAnswerToSocks4() {
        var ansAck = new byte[8];
        ansAck[0] = 0;
        ansAck[1] = 91;
        ansAck[2] = 0;
        ansAck[3] = 0;
        ansAck[4] = 0;
        ansAck[5] = 0;
        ansAck[6] = 0;
        ansAck[7] = 0;
        return ansAck;
    }
}
