<html xmlns="http://www.w3.org/1999/xhtml">
<body style="margin: 0; padding: 0; background-color: #F2F3F5;">
    <table align="center" border="0" cellpadding="0" cellspacing="0" width="600" style="margin: 30px auto; background-color: #ffffff; border-radius: 15px;">
        <tr>
            <td align="center" bgcolor="#FFA000" style="padding: 40px; color: #ffffff; font-size: 28px; font-weight: bold; border-top-left-radius: 15px; border-top-right-radius: 15px; border-bottom: 3px solid #D57D00;">Welcome to Your Customer Account</td>
        </tr>
        <tr>
            <td align="center" style="padding: 30px;"><img src="https://martechseries.com/wp-content/uploads/2020/11/Amazon-Advertising-Demand-Side-Platform-Agency-Clear-Ads-Brings-Powerful-Advertising-Opportunities-to-Businesses.jpg" alt="Merchant Logo" width="550" height="170"></td>
        </tr>
        <tr>
            <td style="padding: 20px; color: #333; font-size: 18px; line-height: 1.5; border-bottom: 2px solid #FFA000;">
                <p style="font-size: 24px; font-weight: bold; color: #FFA000;">Hello ${genderPrefix}, ${customer.getName()},</p>
                <p>Welcome to your merchant account at e-kart. We're excited to have you as our valued partner!</p>
                <p>With our platform, you can showcase your products and reach a wide audience of shoppers.</p>
                <p>This link is valid for 5 minutes. Please use it within this time frame to complete your account setup.</p>
              <p>Valid Till :${formattedOTPExpirationTime}</p>
              	<center><a href="http://localhost:8080/customer/verify-link/${email}/${token}">
    <button style="background-color: #FFA000; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer;">
        Click Here to verify
    </button>
</a>
              	</center>
            </td>
        </tr>
        <tr>
            <td align="center" bgcolor="#F2F3F5" style="padding: 20px; color: #888; font-size: 14px; border-bottom-left-radius: 15px; border-bottom-right-radius: 15px;">&copy; 2023 Welcome to e-kart Customer Portal</td>
        </tr>
    </table>
</body>
</html>
