package minigame_myw;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345;
    private static Map<String, ClientHandler> clients = new HashMap<>(); // 클라이언트 관리

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버 시작...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트 연결됨: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private Player player;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.player = new Player(); // 각 클라이언트 별로 새로운 플레이어 객체 생성
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String nickname = in.readLine();
                player.setNickname(nickname);
                clients.put(nickname, this); // 닉네임을 키로 하여 클라이언트 관리

                // 닉네임 확인 메시지 전송
                out.println("환영합니다. " + nickname + "님!");
                
                // 클라이언트와의 통신 처리
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    processInput(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processInput(String input) {
            String[] parts = input.split(" ");
            switch (parts[0]) {
                case "PLAY_MINIGAME":
                    out.println(player.playMiniGame());
                    break;
                case "GET_CHARACTER_STATUS":
                    out.println(player.getCharacterStatus());
                    break;
                case "GET_PERSONALITY_STATUS":
                    out.println(player.getPersonalityStatus());
                    break;
                case "TAKE_PERSONALITY_TEST":
                    player.takePersonalityTest();
                    out.println("성향 검사를 완료했습니다.");
                    break;
                default:
                    out.println("알 수 없는 입력입니다.");
            }
        }
    }
}

class Player {
    private String nickname;
    private String characterStatus;
    private int miniGamesPlayed; // 몇 개의 미니게임을 진행했는지
    private boolean personalityTestTaken;

    public Player() {
        this.characterStatus = "알이 부화되지 않았습니다."; // 초기 캐릭터 상태
        this.miniGamesPlayed = 0;
        this.personalityTestTaken = false;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String playMiniGame() {
        miniGamesPlayed++;
        return "미니게임을 " + miniGamesPlayed + "번 진행했습니다.";
    }

    public String getCharacterStatus() {
        return characterStatus;
    }

    public String getPersonalityStatus() {
        if (!personalityTestTaken) {
            return "아직 성향 검사를 진행하지 않았습니다.";
        }
        // 성향 검사 결과를 랜덤으로 반환 (예시)
        return Math.random() > 0.5 ? "IF입니다!" : "ET입니다!";
    }

    public void takePersonalityTest() {
        personalityTestTaken = true; // 성향 검사 완료
    }
}