/**
 * Created by Artur Wieczorek on 10.02.17.
 */

import groovy.util.logging.Slf4j
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient

@Grapes([
        @Grab(group='org.apache.ivy', module='ivy', version='2.4.0'),
        @Grab(group='commons-net', module='commons-net', version='3.5'),
        @Grab(group='org.slf4j', module='slf4j-api', version='1.6.1'),
        @Grab(group='ch.qos.logback', module='logback-classic', version='0.9.28')
])
@Slf4j
class FTPUtilities {

    private static FTPClient ftpClient

    static void login(String server, int port = 21, String username = 'ftp_user', String password = 'your_password') throws IOException {
        ftpClient = new FTPClient()

        try {
            ftpClient.connect(server, port)
            ftpClient.login(username, password)
            ftpClient.enterLocalPassiveMode()

            log.info 'Connected and successfully logged to FTP server'
        }
        catch (IOException ex) {
            ex.printStackTrace()
        }
    }

    static void uploadDirectory(String remoteDirPath, String localDirPath) throws IOException {
        log.info 'Listing directory: ' + localDirPath

        File localDir = new File(localDirPath)
        File[] subFiles = localDir.listFiles()

        if (subFiles) {
            for (File item : subFiles) {
                String remoteFilePath = remoteDirPath + '/' + item.getName()

                if (item.isFile()) {
                    // upload file
                    String localFilePath = item.getAbsolutePath()
                    log.info 'Starting upload of file: ' + localFilePath
                    boolean uploaded = uploadFile(localFilePath, remoteFilePath)
                    if (uploaded)
                        log.info 'File uploaded to: ' + remoteFilePath
                    else
                        log.warn 'Could not upload file: ' + localFilePath

                } else {
                    // create directory on the server
                    boolean created = ftpClient.makeDirectory(remoteFilePath)
                    if (created)
                        log.info  'Created directory: ' + remoteFilePath
                    else
                        log.warn 'Could not create directory: ' + remoteFilePath

                    // upload the sub directory
                    localDirPath = item.getAbsolutePath()
                    uploadDirectory(remoteFilePath, localDirPath)
                }
            }
        }
    }

    static boolean uploadFile(String localFilePath, String remoteFilePath) throws IOException {
        File localFile = new File(localFilePath)

        InputStream inputStream = new FileInputStream(localFile)
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
            return ftpClient.storeFile(remoteFilePath, inputStream)
        } finally {
            inputStream.close()
        }
    }

    static void disconnect(){
        ftpClient.logout()
        ftpClient.disconnect()
    }
}

    String server = 'xyz.xy.xyz.xy'
    String remoteDirPath = ''
    String localDirPath = '/home/artur/Documents/TestFtpUpload'

    FTPUtilities.login(server)
    FTPUtilities.uploadDirectory(remoteDirPath, localDirPath)
    FTPUtilities.disconnect()