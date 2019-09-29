package binchunk;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            byte[] data = Files.readAllBytes(Paths.get(args[0]));
            Prototype proto = BinaryChunk.undump(data);
//            list(proto);
        }
    }
}
