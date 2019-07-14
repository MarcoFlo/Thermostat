package it.polito.thermostat.controllermd.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import it.polito.thermostat.controllermd.configuration.HostAddressGetter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
public class QRService {

    /**
     * @return the image to get the thermostat controller from the phone
     * @throws WriterException
     * @throws IOException
     */
    public byte[] getQRCodeImage() throws WriterException, IOException {
        String text = "http://" + HostAddressGetter.getIp() + ":8080";
        int size = 500;

        BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();

    }

    /**
     * RestController byte array
     *
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public ResponseEntity<byte[]> getQRCodeByte() throws WriterException, IOException {
        String text = "http://" + HostAddressGetter.getIp() + ":8080";
        int width = 300;
        int height = width;
        String filePath = "./file.png";

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        //scrittura su file
        //MatrixToImageWriter.writeToPath(bitMatrix, "PNG", FileSystems.getDefault().getPath(filePath));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return new ResponseEntity<>(outputStream.toByteArray(), new HttpHeaders(), HttpStatus.OK);

    }
}