package kr.or.kosta.koback.view.chatroom;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class MyCellRenderer implements ListCellRenderer<String> {

   boolean master;
/* public MyCellRenderer(){
	   this(false);
   }
   
   public MyCellRenderer(boolean master) {
	   this.master =master;
   }
*/
   
   @Override
   // JList에 의해 자동 호출되는 콜백메소드 (규약 메소드가 1개 뿐이다)
   // 반환하는 Component = Renderer를 의미
   /**
    * @list JList를 받는다
    * @value JList가 가진 value
    * @index JList의 순서
    * @isSelected JList 선택된 거
    * @cellHasFocus 포커스(마우스커서 등)....
    */
   public Component getListCellRendererComponent(JList<? extends String> list,
                                      String value, int index, boolean isSelected, boolean cellHasFocus) {
     
	   
	  
     JLabel render = new JLabel();
     String[] values = value.split("\\|");
     String userId = values[0];
     String captain = values[1];
      render.setText(userId);
      if(new Boolean(captain)){
    	  URL kUrl = getClass().getResource("/images/king.png");
         render.setIcon(new ImageIcon(kUrl)); 		/*변경 0824 조현빈*/
      }else{
    	  URL bUrl = getClass().getResource("/images/beggar.png");
         render.setIcon(new ImageIcon(bUrl));	/*변경 0824 조현빈*/
      }
      
      if(isSelected){
         render.setOpaque(true); // 투명하게 해줘야 background색이 먹는다
         render.setBackground(Color.LIGHT_GRAY);
         render.setForeground(Color.yellow);
      }
      return render;
   }
   
  
}