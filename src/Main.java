package com.company;

import javafx.scene.control.Alert;
import org.w3c.dom.css.RGBColor;
import sun.awt.image.PixelConverter;
import sun.security.util.BitArray;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List.*;

public class Main {

    private static BitArray ByteToBit(byte src){
        BitArray bitArray = new BitArray(8);
        boolean st=false;
        for (int i=0;i<8;i++){
            if ((src>>i&1)==1)
                st=true;
            else st=false;
            bitArray.set(i,st);
        }
        return bitArray;
    }

    private static byte BitToByte(BitArray src){
        byte num=0;
        for (int i=0;i<src.length();i++)
            if (src.get(i)==true)
                num+=Math.pow(2,i);
        return num;
    }

    private static  boolean isEncryption(BufferedImage src){
        byte []rez=new byte[1];
        Color color = new Color(src.getRGB(0,0));
        BitArray colorArray = ByteToBit((byte)color.getRed());
        BitArray messageArray = ByteToBit((byte)color.getRed());
        messageArray.set(0,colorArray.get(0));
        messageArray.set(1,colorArray.get(1));
        colorArray=ByteToBit((byte)color.getGreen());
        messageArray.set(2,colorArray.get(0));
        messageArray.set(3,colorArray.get(1));
        messageArray.set(4,colorArray.get(2));
        colorArray=ByteToBit((byte)color.getBlue());
        messageArray.set(5,colorArray.get(0));
        messageArray.set(6,colorArray.get(1));
        messageArray.set(7,colorArray.get(2));
        rez[0]=BitToByte(messageArray);
        String m;
        if (rez[0]<0) {
            byte a=(byte)(-127-rez[0]);
            m=String.valueOf(a);
        }
        else m=rez.toString();
        if (m=="/" || rez[0]==-48)
            return true;
        return false;
    }

    private static void WriteCountText(int count, BufferedImage src){
        byte []CountSymbols = String.valueOf(count).getBytes();
        for (int i=0;i<CountSymbols.length;i++){
            BitArray bitCount = ByteToBit(CountSymbols[i]);
            Color pColor = new Color(src.getRGB(0,i+1));
            BitArray bitsCurColor = ByteToBit((byte)pColor.getRed());
            bitsCurColor.set(0,bitCount.get(0));
            bitsCurColor.set(1,bitCount.get(1));
            byte nR = BitToByte(bitsCurColor);
            bitsCurColor=ByteToBit((byte)pColor.getGreen());
            bitsCurColor.set(0,bitCount.get(2));
            bitsCurColor.set(1,bitCount.get(3));
            bitsCurColor.set(2,bitCount.get(4));
            byte nG=BitToByte(bitsCurColor);
            bitsCurColor = ByteToBit((byte)pColor.getBlue());
            bitsCurColor.set(0,bitCount.get(5));
            bitsCurColor.set(1,bitCount.get(6));
            bitsCurColor.set(2,bitCount.get(7));
            byte nB = BitToByte(bitsCurColor);
            int nnR=nR,nnG=nG,nnB=nB;
            if (nR<0)
                nnR=127-nR;
            if (nB<0)
                nnB=127-nB;
            if (nG<0)
                nnG=127-nG;
            Color nColor = new Color(nnR,nnG,nnB);
            src.setRGB(0,i+1,nColor.getRGB());
        }
    }

    private static int ReadCountText(BufferedImage src){
        byte []rez=new byte[100];
        for (int i=0;i<100;i++) {
            Color color = new Color(src.getRGB(0, i + 1));
            BitArray colorArray = ByteToBit((byte) color.getRed());
            BitArray bitCount = ByteToBit((byte) color.getRed());
            bitCount.set(0, colorArray.get(0));
            bitCount.set(1, colorArray.get(1));
            colorArray = ByteToBit((byte) color.getGreen());
            bitCount.set(2, colorArray.get(0));
            bitCount.set(3, colorArray.get(1));
            bitCount.set(4, colorArray.get(2));
            colorArray = ByteToBit((byte)color.getBlue());
            bitCount.set(5,colorArray.get(0));
            bitCount.set(6,colorArray.get(1));
            bitCount.set(7,colorArray.get(2));
            rez[i]=BitToByte(bitCount);
        }
        int []rezz=new int[100];
        for (int i=0;i<100;i++)
            if (rez[i]<0)
                rezz[i]=127-rez[i];
            else rezz[i]=rez[i];
        String m = rezz.toString();
        String number="";
        for (int i=0;i<m.length();i++)
            if (m.charAt(i)>='0' && m.charAt(i)<='9')
                number+=m.charAt(i);
        return Integer.parseInt(number);
    }

    private static void OpenFileCode(String inputPicName, String inputTextName, String outputPicName) throws Exception{
        BufferedImage bPic = ImageIO.read(new File(inputPicName));
        RandomAccessFile bText = new RandomAccessFile(new File(inputTextName),"r");
        ArrayList<Byte> bList = new ArrayList<>();
        int ii=0;
        while (ii<bText.length()){
            bList.add(bText.readByte());
            ii++;
        }
        int CountText = bList.size();
        bText.close();
        if (CountText>((bPic.getWidth()*bPic.getHeight()))-4){
            System.out.println("Выбранная картинка мала для размещения выбранного текста");
            return;
        }
        if (isEncryption(bPic)){
            System.out.println("Файл уже зашифрован");
            return;
        }
        byte []Symbol = "/".getBytes();
        BitArray ArrBeginSymbol = ByteToBit(Symbol[0]);
        Color curColor = new Color(bPic.getRGB(0,0));
        BitArray tempArray = ByteToBit((byte)curColor.getRed());
        tempArray.set(0,ArrBeginSymbol.get(0));
        tempArray.set(1,ArrBeginSymbol.get(1));
        byte nR = BitToByte(tempArray);
        tempArray.set(0,ArrBeginSymbol.get(2));
        tempArray.set(1,ArrBeginSymbol.get(3));
        tempArray.set(2,ArrBeginSymbol.get(4));
        byte nG = BitToByte(tempArray);
        tempArray = ByteToBit((byte)curColor.getBlue());
        tempArray.set(0,ArrBeginSymbol.get(5));
        tempArray.set(1,ArrBeginSymbol.get(6));
        tempArray.set(2,ArrBeginSymbol.get(7));
        byte nB = BitToByte(tempArray);

        int nnR=nR,nnG=nG,nnB=nB;
        if (nR<0)
            nnR=127-nR;
        if (nB<0)
            nnB=127-nB;
        if (nG<0)
            nnG=127-nG;
        Color nColor = new Color(nnR,nnG,nnB);
        bPic.setRGB(0,0, nColor.getRGB());
        WriteCountText(CountText,bPic);
        int index=0;
        boolean st=false;
        for (int i=4;i<bPic.getWidth();i++){
            for (int j=0;j<bPic.getHeight();j++){
                Color pixelColor = new Color(bPic.getRGB(i,j));
                if (index==bList.size()){
                    st=true;
                    break;
                }
                BitArray colorArray = ByteToBit((byte)pixelColor.getRed());
                BitArray messageArray = ByteToBit(bList.get(index));
                colorArray.set(0,messageArray.get(0));
                colorArray.set(1,messageArray.get(1));
                byte newR = BitToByte(colorArray);
                colorArray = ByteToBit((byte)pixelColor.getGreen());
                colorArray.set(0,messageArray.get(2));
                colorArray.set(1,messageArray.get(3));
                colorArray.set(2,messageArray.get(4));
                byte newG = BitToByte(colorArray);
                colorArray = ByteToBit((byte)pixelColor.getBlue());
                colorArray.set(0,messageArray.get(5));
                colorArray.set(1,messageArray.get(6));
                colorArray.set(2,messageArray.get(7));
                byte newB = BitToByte(colorArray);
                nnR=newR;nnG=newG;nnB=newB;
                if (newR<0)
                    nnR=127-newR;
                if (newB<0)
                    nnB=127-newB;
                if (newG<0)
                    nnG=127-newG;
                Color newColor = new Color(nnR,nnG,nnB);
                bPic.setRGB(i,j,newColor.getRGB());
                index++;
            }
            if (st)
                break;
        }
        ImageIO.write(bPic,"bmp",new File(outputPicName));
    }

    public static void OpenFileDecode(String outputPicName,String outputTextName) throws Exception{
        BufferedImage bPic = ImageIO.read(new File(outputPicName));
        if (!isEncryption(bPic)){
            System.out.println("В файле нет зашифрованной информации");
            return;
        }
        int countSymbol = ReadCountText(bPic);
        byte[] message = new byte[countSymbol];
        int index = 0;
        boolean st=false;
        for (int i=4;i<bPic.getWidth();i++){
            for (int j=0;j<bPic.getHeight();j++){
                Color pixelColor  = new Color(bPic.getRGB(i,j));
                if (index==message.length){
                    st=true;
                    break;
                }
                BitArray colorArray = ByteToBit((byte)pixelColor.getRed());
                BitArray messageArray = ByteToBit((byte)pixelColor.getRed());
                messageArray.set(0,colorArray.get(0));
                messageArray.set(1,colorArray.get(1));
                colorArray = ByteToBit((byte)pixelColor.getGreen());
                messageArray.set(2,colorArray.get(0));
                messageArray.set(3,colorArray.get(1));
                messageArray.set(4,colorArray.get(2));
                colorArray = ByteToBit((byte)pixelColor.getBlue());
                messageArray.set(5,colorArray.get(0));
                messageArray.set(6,colorArray.get(1));
                messageArray.set(7,colorArray.get(2));
                message[index]=BitToByte(messageArray);
                index++;
            }
            if (st){
                break;
            }
            String strMessage="";
            for (int ii=0;ii<index;ii++) {
                if (message[ii] < 0)
                    strMessage += (char) (Math.abs(message[ii])-1);
                else strMessage += (char) message[ii];
            }
            BufferedWriter write = new BufferedWriter(new FileWriter(outputTextName));
            write.write(strMessage);
            write.close();
        }
    }

    public static void main(String[] args) {
        try {
            /*Путь к изображению в котором скрывать*/
            String inputPicName="C:\\Users\\Лиор\\Desktop\\1.bmp";
            /*путь к файлу который скрывать*/
            String inputTextName="C:\\Users\\Лиор\\Desktop\\11.txt";
            /*Путь к изображению для расшифровки*/
            String outputPicName="C:\\Users\\Лиор\\Desktop\\3.bmp";
            /*Путь к тектсовому файлу, в котором будет расшифровка*/
            String outputTextName="C:\\Users\\Лиор\\Desktop\\test.txt";
            OpenFileCode(inputPicName,inputTextName,outputPicName); //сокрытие
            OpenFileDecode(outputPicName,outputTextName); //расшифровка
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
