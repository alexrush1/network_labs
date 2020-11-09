package treechat.model;

import lombok.Getter;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
public class ChatSettings {
    private static final Logger LOGGER = Logger.getLogger(ChatSettings.class.getName());
    private static final String chatSettingPath = "/chatSettings.properties";
    private int resendDelay;
    private int removingTime;
    private int confirmMessageSize;
    private int sentMessageStorageSize;
    private int messageToSendStorageSize;
    private int receivedMessageStorageSize;
    private int maxChatByteSize;
    private int senderTimeout;
    private int aliveTime;

    private ChatSettings() {
        Properties properties = new Properties();
        try (InputStream inputStream = ChatSettings.class.getResourceAsStream(chatSettingPath)) {
            properties.load(inputStream);
            init(properties);
        } catch (IOException e) {
            LOGGER.severe("No such configure property:" + chatSettingPath);
        }

    }

    public static ChatSettings getInstance() {
        return Handler.INSTANCE;
    }

    private void init(Properties properties) {
        resendDelay = Integer.parseInt(properties.getProperty("resendDelay"));
        removingTime = Integer.parseInt(properties.getProperty("removeTime"));
        maxChatByteSize = Integer.parseInt(properties.getProperty("maxChatByteSize"));
        confirmMessageSize = Integer.parseInt(properties.getProperty("confirmMessageSize"));
        sentMessageStorageSize = Integer.parseInt(properties.getProperty("sentMessageStorageSize"));
        messageToSendStorageSize = Integer.parseInt(properties.getProperty("messageToSendStorageSize"));
        receivedMessageStorageSize = Integer.parseInt(
                properties.getProperty("receivedMessageStorageSize"));
        senderTimeout = Integer.parseInt(properties.getProperty("senderTimeout"));
        aliveTime = Integer.parseInt(properties.getProperty("aliveTime"));
    }

    private static class Handler {
        static final ChatSettings INSTANCE = new ChatSettings();
    }
}
