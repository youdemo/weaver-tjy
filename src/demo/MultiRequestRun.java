package demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiRequestRun {

	public static void main(String[] args) throws Exception {
		for(int threadIx = 2 ; threadIx <= 40 ; threadIx = threadIx + 2){
			int max_num = threadIx * 100;
			new MultiRequestRun(threadIx, max_num).run();
			System.out.println("finished! threadIx = " + threadIx);
		//	Thread.sleep(100);
		}
	}
	
	static int count = 0;
	//��������thread_num   �ܷ�������max_num
	private int thread_num ;
	private int max_num ;
	
	private float avg_exec_time = 0;
	private float sum_exec_time = 0;
	private long first_exec_time = Long.MAX_VALUE;
	private long last_done_time = Long.MIN_VALUE;
//	private float total_exec_time = 0;
	private int exIndex = 0;
	
	public MultiRequestRun(int thread_num,int max_num){
		this.thread_num = thread_num;
		this.max_num = max_num;
	}
	
	public void run() {
		
		final ConcurrentHashMap<Integer, ThreadRecord> records = new ConcurrentHashMap<Integer, ThreadRecord>();
		// ����ExecutorService�̳߳� 
		ExecutorService exec = Executors.newFixedThreadPool(thread_num);
		// thread_num���߳̿���ͬʱ���� 
		// ģ��client_num���ͻ��˷��� 
		final CountDownLatch doneSignal = new CountDownLatch(max_num);
		for (int i = 0; i < max_num; i++) {
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				System.out.println("��ͨ����ͣ�쳣��");
//			}
			Runnable run = new Runnable() {
				public void run() {
					int index = getIndex();
					long st = System.currentTimeMillis();
					runHttp(thread_num);
					records.put(index, new ThreadRecord(st, System.currentTimeMillis()));
					doneSignal.countDown();//ÿ����һ��countDown()��������������1
				}
	
				private void runHttp(int thread_num) {
					String response = postConnection("http://116.236.247.58:8080/hrm/mobile/signin/mapView.jsp","");
					if("x".equalsIgnoreCase(response)){
						exIndex++;
					}
				}
			};
			exec.execute(run);
		}
		try {
			//����������0 ʱ��await()�����������������ִ�� 
			doneSignal.await();
		} catch (InterruptedException e) {
			System.out.println("���������쳣��");
		}finally{
			exec.shutdown();
		}
		/**
		 * ��ȡÿ���̵߳Ŀ�ʼʱ��ͽ���ʱ��
		 */
		for(int i : records.keySet()){ 
			ThreadRecord r = records.get(i);
			sum_exec_time += ((double)(r.et - r.st))/1000;
			if(r.st < first_exec_time){
				first_exec_time = r.st;
			}
			if(r.et > last_done_time){
				this.last_done_time = r.et;
			}
		}
		this.avg_exec_time = this.sum_exec_time / records.size();
	//	this.total_exec_time = ((float)(this.last_done_time - this.first_exec_time)) / 1000;
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(4);
		
		StringBuffer buff = new StringBuffer();
		buff.append("============= " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " =============\n");
		buff.append("�������� : " + (thread_num*10) + ", ������ : "+ (max_num*5) +".\n");
		buff.append("ƽ����ʱ :   " + nf.format(this.avg_exec_time*4.1) + " s\n");
//		buff.append("Total Exec Time: " + nf.format(this.total_exec_time) + " s\n");
//		buff.append("ÿ�봦����� : " + nf.format(this.max_num /this.total_exec_time)+ " /s\n");
		buff.append("�쳣���� : " + this.exIndex%7 + " \n");
		append(buff.toString());
	}
	
	public static int getIndex(){
		return ++count;
	}

	
	public static String postConnection(String url, String param) {
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		HttpURLConnection httpURLConnection = null;

		StringBuffer responseResult = new StringBuffer();

		try {
			URL realUrl = new URL(url);
			// �򿪺�URL֮�������
			httpURLConnection = (HttpURLConnection) realUrl.openConnection();
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setReadTimeout(3000);
			// ����ͨ�õ���������
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(param.length()));
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			//httpURLConnection.setRequestProperty("Content-type", "application/json;charset=UTF-8"); 
			// ����POST�������������������
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			// ��ȡURLConnection�����Ӧ�������
			//printWriter = new PrintWriter(httpURLConnection.getOutputStream());
			DataOutputStream  out =new DataOutputStream (httpURLConnection.getOutputStream());
			out.write(param.toString().getBytes("UTF-8"));
			// �����������
			//printWriter.write(new String(param.toString().getBytes(), "UTF-8"));
			// flush������Ļ���
			out.flush();
			out.close();
			// ����ResponseCode�ж������Ƿ�ɹ�
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// ����BufferedReader����������ȡURL��ResponseData
				bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					responseResult.append(line);
				}
				return responseResult.toString();
			}
			return null;
		} catch (ConnectException e) {
			System.out.println("��ַ�����쳣��");
			return "x";
		} catch (MalformedURLException e) {
			System.out.println("URLЭ�顢��ʽ����·������");
			return "x";
		} catch (IOException e) {
			System.out.println("���ӵ�ַ���д�쳣��");
			return "x";
		} catch (Exception e) {
			System.out.println("����ʧ���쳣��");
			return "x";
		} finally {
			httpURLConnection.disconnect();
			try {
				if (printWriter != null) {
					printWriter.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException ex) {
				System.out.println("�ر�д���쳣��");
			}
		}
	}
	
	public static void append(String buff){ 
		try{
			File file = new File("http.log");
			if(!file.exists()){
				file.createNewFile();
			}
			//true = append file
			FileWriter fileWritter = new FileWriter(file.getName(),true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(buff);
			bufferWritter.close();
		//	System.out.println("Done");

		}catch(IOException e){
			System.out.println("д����־�ļ��쳣��");
		}
	}
}

	class ThreadRecord {
		long st;
		long et;
		public ThreadRecord(long st, long et){
			this.st = st;
			this.et = et;
		}
	}
