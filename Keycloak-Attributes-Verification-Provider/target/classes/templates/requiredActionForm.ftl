<form action="${context.getActionUrl()}" method="post">
    <input type="hidden" name="otp" value="${uav.otp}">
    <input type="text" name="otpInput" placeholder="Enter OTP">
    <button type="submit">Submit</button>
</form>