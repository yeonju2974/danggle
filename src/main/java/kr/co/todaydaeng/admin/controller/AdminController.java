package kr.co.todaydaeng.admin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import kr.co.todaydaeng.admin.model.service.AdminService;
import kr.co.todaydaeng.admin.model.service.ManageService;
import kr.co.todaydaeng.admin.model.vo.AdminVO;
import kr.co.todaydaeng.member.model.vo.Member;

@Controller
public class AdminController {
	
	@Autowired
	private AdminService aService;
	
	@Autowired
	private ManageService mService;

	@RequestMapping(value="/admin/adminIndex.do", method = RequestMethod.GET)
		public String adminIndex (HttpSession session) {
		//기존 댕댕에서의 세션을 초기화
		session.invalidate();
		
		//관리자 로그인 페이지로 리다이렉트		
		return "adminView/adminLogin";
		}
	
	
	@RequestMapping(value="/admin/adminLogin.do", method = RequestMethod.POST)
	public void selectAdminLogin(HttpSession session, HttpServletResponse response,
							@RequestParam String adminID, @RequestParam String adminPWD) throws IOException {
						
				//Request Param 인자의 유효성 검사 
				if (adminID == null || adminPWD == null) {
					response.getWriter().print(false);
					
				}else {		
					
					HashMap<String, String> map = new HashMap<String, String>();
					
					map.put("adminID",adminID);
					map.put("adminPWD",adminPWD);
								
				AdminVO adm = aService.selectAdminLogin(map);
				
				if (adm != null) {			
					session.setAttribute("adminVO", adm);
					response.getWriter().print(true);				
				}else {
					// return되는 정보가 없으면 false 반환, 로그인 페이지 그대로 유지
					response.getWriter().print(false);
				}	
		
			}
	}
	
	@RequestMapping(value="/admin/adminLogout.do", method = RequestMethod.GET)
	public String adminLogout(HttpSession session) {
		session.invalidate();
		
		return "redirect:/";
	}
	
	@RequestMapping(value="/admin/adminMain.do", method = RequestMethod.GET)
	public String adminMain() {
		return "adminView/adminMain";
	}
	
	@RequestMapping(value="/admin/adminAccount.do")
	public String adminAccount() {
		return "adminView/adminAccount";		
	}
	
	@RequestMapping(value="/admin/adminIDCheck.do", method = RequestMethod.POST)
	public void selectAdminIDCheck(@RequestParam String chkID, HttpServletResponse response) throws IOException {		
		
		//ID중복 확인값의 유효성 검사
		if (chkID == null || chkID.length() > 15) {
			response.getWriter().print("invalid");
		}else {
			String result = mService.selectAdminIDCheck(chkID);						
			
			if (result == null) {
				response.getWriter().print("pass");
			}else {
				response.getWriter().print("false");
			}
		}
	}
	
	@RequestMapping(value="/admin/insertAdminAccount.do", method = RequestMethod.POST)
	public void insertAdminAccount(AdminVO avo, HttpServletResponse response) throws IOException {
		String id = avo.getAdminID();
		String pwd = avo.getAdminPWD();
		String name = avo.getAdminName();
		String email = avo.getAdminEmail();
		
		//유효성 검사
		if ( id == null || pwd == null || name == null || email == null || name.length() <2 || name.length() > 5 || email.length() > 31 || !(email.contains("@")) || pwd.length() > 21 ) {
			response.getWriter().print("invalid");
		}else {
			int result = mService.insertAdminAccount(avo);
			
			if (result >0) {
				response.getWriter().print("pass");
			}else {
				response.getWriter().print("false");
			}
		}		
	}
	
	@RequestMapping(value="/admin/adminInfo.do")
	public String adminInfo() {
		return "adminView/adminInfo";		
	}

	@RequestMapping(value="/admin/adminEmailCheck.do", method=RequestMethod.POST)
	public void selectAdminEmailCheck(@RequestParam String chkMail, HttpServletResponse response) throws IOException {
		
		//email중복 확인값의 유효성 검사
		if (chkMail == null || chkMail.length() > 30 ) {
			response.getWriter().print("invalid");
		}else {
			String result = aService.selectAdminEmailCheck(chkMail);						
			
			if (result == null) {
				response.getWriter().print("pass");
			}else {
				response.getWriter().print("false");
			}
		}
	}
	
	@RequestMapping(value="/admin/updatePWDChange.do", method=RequestMethod.POST)
	public void updateAdminPWD(@RequestParam String adminID, @RequestParam String oldPWD, 
								@RequestParam String newPWD, HttpServletResponse response)  throws IOException {
		
		// 로그인 메소드를 호출하여 사용자 검증 후에 암호 변경 로직을 수행한다
		HashMap<String, String> map = new HashMap<String, String>();		
		map.put("adminID",adminID);
		map.put("adminPWD",oldPWD);

		AdminVO adm = aService.selectAdminLogin(map);
	
		if (adm == null)  {
			response.getWriter().print("invalid");
			
		}else {
		//사용자 검증-로그인 메소드를 통과했다면 암호 변경 로직을 수행
		HashMap mapPwd = new HashMap<String, String>();		
		mapPwd.put("adminID", adminID);		
		mapPwd.put("newPWD", newPWD);		
			int result = aService.updateAdminPWD(mapPwd);						
			
			if (result >0) {
				response.getWriter().print("true");
			}else {
				response.getWriter().print("false");
			}
		}	
	}	
	
	@RequestMapping(value="/admin/updateAdminAccount.do", method=RequestMethod.POST)
	public void updateAdminAccount(AdminVO avo, HttpServletResponse response, HttpSession session) throws IOException {						
		String name = avo.getAdminName();
		String email = avo.getAdminEmail();
		String adminID = avo.getAdminID();
		
		//유효성 검사
		if ( name == null || email == null || name.length() <2 || name.length() > 5 || email.length() > 31 || !(email.contains("@")) ) {
			response.getWriter().print("invalid");

		}else {	
			String chkMail = email;
			String chk = aService.selectAdminEmailCheck(chkMail);
			if (chk != null) {
				response.getWriter().print("invalid");
			}else {
			
			int result = aService.updateAdminAccount(avo);
			
			if (result >0) {
				
				//업데이트 끝나면 select query 실행해서 session을 갱신
				AdminVO refresh = aService.selectAdminAccount(adminID);
				session.setAttribute("adminVO",refresh);				
				response.getWriter().print("pass");
			}else {
				response.getWriter().print("false");
			}
		  }
		}	
	}
	
	@RequestMapping(value="/admin/adminManage.do")
	public ModelAndView adminManage(ModelAndView mav) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<AdminVO> list = aService.selectAdminList();
		map.put("admin", list);
		
		int newbie = aService.selectAdminNew();
		map.put("count", newbie);

		mav.addObject("map",map);		
		mav.setViewName("adminView/adminManage");
		
		return mav;		
	}
	
	@RequestMapping(value="/admin/adminGradeChange.do", method=RequestMethod.POST)
	public void updateAdminGrade(@RequestParam(value="dataArray[]")ArrayList<Integer> dataArray, @RequestParam String newGrade, 
								HttpServletResponse response ) throws IOException {

		if(dataArray == null || newGrade == null) {
			response.getWriter().print(false);
		}else {
			HashMap<String, Object> map = new HashMap<String, Object>();		
			map.put("newGrade",newGrade);
			map.put("adminNo",dataArray);
			int result = mService.updateAdminGrade(map);

		if(result == dataArray.size()) {
			response.getWriter().print("pass");
		}else {
			response.getWriter().print("false");
		}
	   }
	}
		

	@RequestMapping(value="/admin/manageSearch.do", method=RequestMethod.GET)
	public ModelAndView selectAdminSearch(@RequestParam String tag, @RequestParam String keyword,
								  ModelAndView mav) {

		System.out.println("검색분류: "+tag);
		System.out.println("검색대상: "+keyword);		
		
		HashMap<String, String> findMap = new HashMap<String, String>();		
		findMap.put("keyword",keyword);
		findMap.put("tag",tag);		
		ArrayList<AdminVO> list = aService.selectAdminSearch(findMap);
		
		HashMap<String, Object> map = new HashMap<String, Object>();		
		map.put("admin", list);		
		int newbie = aService.selectAdminNew();
		map.put("count", newbie);

		mav.addObject("map",map);		
		mav.setViewName("adminView/adminManage");	
		
		return mav;
	}
	
	public String memberPageNavi(int currentPage, int recordCountPerPage, int naviCountPerPage) {		
		// 전체 회원 수 계산
		int recordTotalCount = aService.selectMemberCount();
		// 전체 페이지 수 계산
		int pageTotalCount = 0;				
		pageTotalCount = (int)Math.ceil(recordTotalCount/(double)recordCountPerPage);
		
		//페이지 네비 시작점
		int startNavi = ( ((currentPage-1)/naviCountPerPage) * naviCountPerPage) +1;
		//페이지 네비 종료점
		int endNavi = startNavi + (naviCountPerPage-1);
		
		//페이지 네비의 종료점이 전체 페이지 수를 초과하지 않게 출력
		if ( endNavi > pageTotalCount) endNavi = pageTotalCount;
		
		//페이지 네비의 HTML 요소를 String Builder로 작성
		StringBuilder sb = new StringBuilder();								
		
		// 페이지 네비가 1페이지 시작이 아니라면 HTML요소를 뒤로가기로 생성
		if (startNavi != 1) sb.append("<li class='page-item'> <a class='page-link' href='/admin/memberManage.do?currentPage="+(startNavi-1)+"' aria-label='Previous'> ◀  </a> </li>");
			
			// 페이지 네비를 구성하는 HTML 요소를 반복문으로 생성
		for (int i=startNavi; i <= endNavi; i++) {
			//현재 페이지는 볼드
			if (i == currentPage) sb.append("<li class='page-item'><a class='page-link' href='/admin/memberManage.do?currentPage="+i+"'><B style='font-size:1.2em'>"+i+"</B></a></li>");
			else sb.append("<li class='page-item'><a class='page-link' href='/admin/memberManage.do?currentPage="+i+"'>"+i+"</a></li>");				
		}
		
		//페이지 네비가 마지막 이 아니라면 HTML 요소로 앞으로 가기를 생성				
		if (endNavi != pageTotalCount) sb.append("<li class='page-item'> <a class='page-link' href='/admin/memberManage.do?currentPage="+(endNavi+1)+"' aria-label='Next'> ▶  </a> </li>");				
		
		//String Builder로 만든 HTML 요소를 String으로 return
		return sb.toString();
	}
	
	@RequestMapping(value="/admin/memberManage.do")
	public ModelAndView memberManage(ModelAndView mav,HttpServletRequest request) {
		// 1. 사용자 목록과 페이징 처리를 위한 매개변수 저장
		int currentPage = Integer.parseInt(request.getParameter("currentPage") );
		int recordCountPerPage =5;		
		if (currentPage == 0 ) currentPage =1;
		else currentPage = Integer.parseInt(request.getParameter("currentPage") );
		
		//시작 페이지 쪽수
		int first = currentPage * recordCountPerPage - (recordCountPerPage -1);
		
		//마지막 페이지 쪽수
		int last = currentPage * recordCountPerPage;
		
		HashMap<String, Integer> pageNum = new HashMap<String, Integer>();
		pageNum.put("first", first);
		pageNum.put("last", last);
		ArrayList<Member> list = aService.selectMemberList(pageNum);
					
		// 페이징 처리된 목록을 위한 페이지 네비 생성	
		// 게시판 페이지 네비 호출
		int naviCountPerPage = 5;				
		String pageNavi = memberPageNavi(currentPage, recordCountPerPage, naviCountPerPage);
		
		int start = 1;
		int end = aService.selectMemberCount();
		

		// 2. 페이징 처리 된 목록을 model 객체에 저장
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("list",list);
		map.put("pageNavi", pageNavi);
		map.put("currentPage",currentPage);	
		
		map.put("start",start);
		map.put("end",end);
		
		// 3. VIEW로 전송
		mav.addObject("map",map);
		mav.setViewName("adminView/memberManage");		
		return mav;				
	}
	
}
