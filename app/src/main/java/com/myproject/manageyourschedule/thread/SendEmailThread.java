package com.myproject.manageyourschedule.thread;

import com.myproject.manageyourschedule.util.GMailSender;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import static com.myproject.manageyourschedule.activities.FindPasswordActivity.code;
//로딩스레드
public class SendEmailThread implements Runnable{
    private GMailSender gMailSender;
    // 이메일 인증 코드
    //private String code;
    private String email;
    private String TAG = "SendEmailThread";

    public SendEmailThread(GMailSender gMailSender, String email){
        this.gMailSender=gMailSender;
        //this.code=code;
        this.email=email;
    }

    @Override
    public void run() {

        try {
            gMailSender = new GMailSender("", "");

            code = gMailSender.getEmailCode();

            //GMailSender.sendMail(제목, 본문내용, 받는사람);
            gMailSender.sendMail("Manage Your Schedule 인증번호입니다!", "인증코드는 " + code+ "입니다!" , email);


            //Toast.makeText(TAG, "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            //Toast.makeText(TAG, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            //Toast.makeText(TAG, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
