<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        Profile Attribute Verification
    <#elseif section = "form">
    <div>
        <p>Please verify your ${attributeConfig.displayName?lower_case}:</p>
        <h3>${attributeValue}</h3>
    </div>
    <hr />
    <form class="form-actions" action="${url.loginAction}" method="POST" onsubmit="javascript:setTimeout(() => { document.querySelector('button').setAttribute('disabled', true); document.getElementById('continue-spinner').style.display = ''; var errdiv = document.getElementById('input-error'); if (errdiv) errdiv.style.display = 'none'; }, 100);" style="position:relative">
        <p class="instruction">${challengeMessage?no_esc}</p>
        <#if !challengeSent>
            <button class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="action" id="kc-accept" type="submit" value="generate-send-otp">${msg("doContinue")}</button>
            <#if challengeError??>
                <div id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                        ${challengeError?no_esc}
                </div>
            </#if>
        <#else>
            <div class="${properties.kcFormGroupClass!}">
                <label for="challenge" class="${properties.kcLabelClass!}">Enter the challenge value that you have received</label>
                <input id="challenge" class="${properties.kcInputClass!}" name="challenge" autocomplete="off" type="text" autofocus
                       aria-invalid="<#if messagesPerField.existsError('challenge')>true</#if>"
                />
                <#if challengeError??>
                    <div id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                            ${challengeError?no_esc}
                    </div>
                </#if>
            </div>
            <button class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="action" id="kc-accept" type="submit" value="verify">${msg("doContinue")}</button>
        </#if>
        <hr />
        <p class="instruction">
            Click ${msg("doCancel")} to remove your ${attributeConfig.displayName?lower_case}. You will need to provide that information if it is still required.
        </p>
        <button class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}" name="action" id="kc-decline" type="submit" value="cancel">${msg("doCancel")}</button>
        <div id="continue-spinner" class="pf-l-bullseye" style="display:none; position:absolute; top:0; left:0; width:100%; background:rgba(255,255,255,0.4)">
            <span class=".pf-l-bullseye__item pf-c-spinner pf-m-xl" role="progressbar" aria-valuetext="Loading..." style="vertical-align:middle">
                <span class="pf-c-spinner__clipper"></span>
                <span class="pf-c-spinner__lead-ball"></span>
                <span class="pf-c-spinner__tail-ball"></span>
            </span>
        </div>
    </form>
    <div class="clearfix"></div>
    </#if>
</@layout.registrationLayout>