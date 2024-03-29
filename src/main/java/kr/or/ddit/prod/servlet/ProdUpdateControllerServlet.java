package kr.or.ddit.prod.servlet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.or.ddit.enumpkg.ServiceResult;
import kr.or.ddit.others.advice.OthersControllerAdvice;
import kr.or.ddit.prod.service.ProdService;
import kr.or.ddit.prod.service.ProdServiceImpl;
import kr.or.ddit.utils.PopulateUtils;
import kr.or.ddit.utils.ValidateUtils;
import kr.or.ddit.validate.UpdateGroup;
import kr.or.ddit.vo.MemberVO;
import kr.or.ddit.vo.ProdVO;
@WebServlet("/prod/prodUpdate.do")
public class ProdUpdateControllerServlet extends HttpServlet{
	private ProdService service = new ProdServiceImpl();
	private OthersControllerAdvice advice = new OthersControllerAdvice();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		advice.addLprodList(req);
		advice.addBuyerList(req);
		String prodId = req.getParameter("what");
		ProdVO prod = service.retrieveProd(prodId);
		req.setAttribute("prod", prod);
		String logicalViewName = "prod/prodEdit";
		req.getRequestDispatcher("/"+logicalViewName+".miles").forward(req, resp);		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		advice.addLprodList(req);
		advice.addBuyerList(req);
		ProdVO prod = new ProdVO();
		req.setAttribute("prod", prod);
		Map<String, String[]> parameterMap = req.getParameterMap();
		PopulateUtils.populate(prod, parameterMap);
		Map<String, String> errors = new LinkedHashMap<>();
		req.setAttribute("errors", errors);
		boolean valid = ValidateUtils.validate(prod, errors, UpdateGroup.class);
		String logicalViewName = null;
		
		if (errors.isEmpty()) {
			// 3. 수정 로직 호출
			ServiceResult result = service.modifyProd(prod);
			// 4. 로직의 실행 결과에 따른 뷰 선택
			String message = null;
			switch (result) {
			case OK:
				logicalViewName = "redirect:/prod/prodView.do?what=" + prod.getProdId();
				break;

			case FAIL:
				logicalViewName = "prod/prodEdit";
				message = "수정 실패";
				break;
			}

			req.setAttribute("message", message);
		} else {
			logicalViewName = "prod/prodEdit";
		}
				
//		5. 해당 뷰로 이동.
		if(logicalViewName.startsWith("redirect:")) {
			String redirectViewPath = req.getContextPath() + logicalViewName.substring("redirect:".length());
			resp.sendRedirect(redirectViewPath);
		}else {
			req.getRequestDispatcher("/"+logicalViewName+".miles").forward(req, resp);
		}
	}
}
