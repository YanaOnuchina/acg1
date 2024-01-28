import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class ObjParser {

    public Model readFile(){
        try {
            String filePath = Objects.requireNonNull(getClass().getClassLoader().getResource("model.obj")).getPath();
            File modelFile = new File(filePath);
            Scanner reader = new Scanner(modelFile);
            Model model = new Model();
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                parseString(data, model);
            }
            reader.close();
            return model;
        }
        catch (FileNotFoundException e) {
            System.out.println("Error during file opening.");
            e.printStackTrace();
        }
        return null;
    }

    public void parseString(String str, Model model){
        String[] dataPieces = str.split(" ");
        switch (dataPieces[0]){
            case "f":
                model.addPolygon(new Polygon(model, Integer.parseInt(dataPieces[1].split("/")[0]), Integer.parseInt(dataPieces[2].split("/")[0]), Integer.parseInt(dataPieces[3].split("/")[0])));
                break;
            case "v":
                model.addVertex(new Vertex(Float.parseFloat(dataPieces[1]), Float.parseFloat(dataPieces[2]), Float.parseFloat(dataPieces[3])));
                break;
        }
    }

}
