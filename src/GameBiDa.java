import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

public class GameBiDa extends JFrame implements Runnable, MouseListener, MouseMotionListener{

	static Bi bi[];
	static int w = 500;
	static int h = 500;
	int off = 50;
	Random rand = new Random();
	BufferedImage img;
	Graphics g;
	
	public static void main(String[] args) {
		new GameBiDa();
	}
	
	public GameBiDa() {
		this.setTitle("Game BiA");
		this.setSize(w+off*2, h+off*2);
		this.setDefaultCloseOperation(3);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		img = new BufferedImage(w+off*2,h+off*2,BufferedImage.TYPE_3BYTE_BGR);
		g = img.getGraphics();
		bi = new Bi[3];
		for (int i=0;i<bi.length;i++) {
			double r = 20;
			double x = rand.nextDouble()*(w-2*r)+r;
			double y = rand.nextDouble()*(h-2*r)+r;
			double vx = (rand.nextDouble()-0.5)*2;
			double vy = (rand.nextDouble()-0.5)*2;
			bi[i] = new Bi(x, y, r, vx, vy);
			bi[i].start();
		}
		
		Thread t = new Thread(this);
		t.start();
		this.setVisible(true);
	}
	
	public void paint(Graphics g1) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g.setColor(Color.BLACK);
		g.drawRect(off, off, w, h);
		
		for (int i=0;i<bi.length;i++) {
			int x = (int)bi[i].x + off;
			int y = (int)bi[i].y + off;
			int r = (int)bi[i].r;
			if(i==0) g.setColor(Color.RED);
			if(i==1) g.setColor(Color.GREEN);
			if(i==2) g.setColor(Color.BLUE);
			g.fillOval(x-r, y-r, r*2, r*2);
			g.setColor(Color.BLACK);
			g.drawOval(x-r, y-r, r*2, r*2);
		}
		if(toadoChuot!=null&&SelectedBi!=null) {
			g.setColor(Color.BLACK);
			g.drawLine((int)SelectedBi.x + off, (int)SelectedBi.y+off, (int)toadoChuot.x, (int)toadoChuot.y);
		}
		g1.drawImage(img, 0,0,null);
	}
	
	@Override
	public void run() {
		while (true) {
			this.repaint();
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(SelectedBi!=null) {
			int x = e.getX();
			int y = e.getY();
			toadoChuot = new Vec(x,y);
//			System.out.println(x+" "+y+"\n");
		}
//		System.out.println("mouse dragged\n");
	}
	
	
	@Override
	public void mouseMoved(MouseEvent e) {
	
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	Bi SelectedBi = null;
	Vec toadoChuot = null;
	
	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX() - off;
		int y = e.getY() - off;
//		System.out.println("mouse press\n");
		for(Bi b : bi) {
			Vec v1 = new Vec(b.x, b.y);
			Vec v2 = new Vec(x,y);
			if(v1.tru(v2).dai()<=b.r) {
				SelectedBi = b;
//				System.out.println(SelectedBi.x +" "+ SelectedBi.y + "\n");
				break;
			}
			SelectedBi = null;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
//		System.out.println("mouse release\n");
		if(SelectedBi!=null&&toadoChuot!=null) {
			Vec tam = new Vec(SelectedBi.x - toadoChuot.x + off, SelectedBi.y - toadoChuot.y + off);
//			System.out.println(tam.dai()+ "\n");
			tam = tam.Nhan(1.0/25);
//			System.out.println(tam.x+" "+tam.y+ "\n");
			SelectedBi.vx = tam.x;
			SelectedBi.vy = tam.y;
		}
		SelectedBi = null;
		toadoChuot = null;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

class Bi extends Thread{
	double x,y;
	double r;
	double vx,vy;
	
	public Bi(double x,double y,double r, double vx, double vy) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.vx = vx;
		this.vy = vy;
	}
	
	public void run() {
		while(true) {
			//Quan tinh
			x+=vx;
			y+=vy;
			
			//Va cham tuong
			if (x<=r && vx<0) vx = -vx;
			if (x+r>=GameBiDa.w && vx>0) vx = -vx;
			if (y<=r && vy<0) vy = -vy;
			if (y+r>=GameBiDa.h && vy>0) vy = -vy;
			
			//va cham bi
			Bi bi[] = GameBiDa.bi;
			synchronized (bi) {
				for (int i=0;i<bi.length;i++) {
					if (bi[i]==null) continue;
					if (this==bi[i]) continue;
					if (distance(this,bi[i])>2*r) continue;
					
					Vec t = new Vec(bi[i].x-this.x, bi[i].y-this.y);
					t = t.Nhan(1.0/t.dai());
					
					Vec v1 = new Vec(this.vx,this.vy);
					Vec v2 = new Vec(bi[i].vx,bi[i].vy);
					Vec v12 = t.Nhan(v1.tichvohuong(t));
					Vec v11 = v1.tru(v12);
					
					Vec v22 = t.Nhan(v2.tichvohuong(t));
					Vec v21 = v2.tru(v22);
					
					if (t.tichvohuong(v12)-t.tichvohuong(v22)>0) 
					{
						Vec v1p = v11.cong(v22);
						Vec v2p = v21.cong(v12);
						this.vx = v1p.x;
						this.vy = v1p.y;
						bi[i].vx = v2p.x;
						bi[i].vy = v2p.y;
					}
				}
			}
			
			// ma sat
			double a = 0.01;
			Vec v1 = new Vec(this.vx, this.vy);
			double v = v1.dai();
			if(v>0) {
				if(v >= a)
					v = v - a;
				else 
					v = 0;
				v1 = v1.Nhan(v/v1.dai());
				this.vx = v1.x;
				this.vy = v1.y;
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
	private double distance(Bi b1, Bi b2) {
		return Math.sqrt((b1.x-b2.x)*(b1.x-b2.x)+(b1.y-b2.y)*(b1.y-b2.y));
	}
}

class Vec{
	double x,y;
	public Vec(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec tru(Vec a) {
		return new Vec(this.x-a.x, this.y - a.y);
	}
	
	public Vec cong(Vec a) {
		return new Vec(this.x+a.x, this.y + a.y);
	}
	
	public double dai() {
		return Math.sqrt(this.x*this.x+this.y*this.y);
	}
	public double tichvohuong(Vec a) {
		return this.x*a.x+this.y*a.y;
	}
	public Vec Nhan(double l) {
		return new Vec(this.x*l, this.y*l);
	}
	
}


