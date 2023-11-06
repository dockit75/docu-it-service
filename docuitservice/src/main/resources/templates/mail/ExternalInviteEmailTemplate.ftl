<!DOCTYPE html>
<html lang="en-US" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml"
    xmlns:o="urn:schemas-microsoft-com:office:office">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="x-apple-disable-message-reformatting">
    <title></title>
    <style>
        html,
        body {
            margin: 0 auto !important;
            padding: 0 !important;
            height: 100% !important;
            width: 100% !important;
            background: white;
            font-family: 'Roboto', sans-serif;
            font-weight: 400;
            font-size: 17px;
            line-height: 1.8;
            color: black;
        }

        button {
            border-radius: 12px;
            border: none;
            background-color: #9ED241;
            color: black;
            position: relative;
            top: 50%;
            padding: 6px;
            text-align: center;
            text-decoration: none;
        }

        .footer-link {
            font-size: 14px;
            color: #ccc;
        }
        .footer-desc{
            height: 5px;
        }

        .amt-table {
            width: 600px !important;
        }

        /* What it does: Stops email clients resizing small text. */
        * {
            -ms-text-size-adjust: 100%;
            -webkit-text-size-adjust: 100%;
        }

        /* What it does: Centers email on Android 4.4 */
        div[style*="margin: 16px 0"] {
            margin: 0 !important;
        }

        /* What it does: Stops Outlook from adding extra spacing to tables. */
        /* table, */
        /* td {
            mso-table-lspace: 0pt !important;
            mso-table-rspace: 0pt !important;
        } */
        /* What it does: Fixes webkit padding issue. */
        /* table {
            border-spacing: 0 !important;
            border-collapse: collapse !important;
            table-layout: fixed !important;
            margin: 0 auto !important;
        } */
        /* What it does: Uses a better rendering method when resizing images in IE. */
        img {
            -ms-interpolation-mode: bicubic;
        }

        /* What it does: Prevents Windows 10 Mail from underlining links despite inline CSS. Styles for underlined links should be inline. */
        a {
            text-decoration: none;
        }

        /* What it does: A work-around for email clients meddling in triggered links. */
        *[x-apple-data-detectors],
        /* iOS */
        .unstyle-auto-detected-links *,
        .aBn {
            border-bottom: 0 !important;
            cursor: default !important;
            color: inherit !important;
            text-decoration: none !important;
            font-size: inherit !important;
            font-family: inherit !important;
            font-weight: inherit !important;
            line-height: inherit !important;
        }

        /* What it does: Prevents Gmail from displaying a download button on large, non-linked images. */
        .a6S {
            display: none !important;
            opacity: 0.01 !important;
        }

        /* What it does: Prevents Gmail from changing the text color in conversation threads. */
        .im {
            color: inherit !important;
        }

        /* If the above doesn't work, add a .g-img class to any image in question. */
        img.g-img+div {
            display: none !important;
        }

        /* What it does: Removes right gutter in Gmail iOS app: https://github.com/TedGoas/Cerberus/issues/89  */
        /* Create one of these media queries for each additional viewport size you'd like to fix */

        /* iPhone 4, 4S, 5, 5S, 5C, and 5SE */
        @media only screen and (min-device-width: 320px) and (max-device-width: 374px) {
            u~div .email-container {
                min-width: 320px !important;
            }
        }

        /* iPhone 6, 6S, 7, 8, and X */
        @media only screen and (min-device-width: 375px) and (max-device-width: 413px) {
            u~div .email-container {
                min-width: 375px !important;
            }
        }

        /* iPhone 6+, 7+, and 8+ */
        @media only screen and (min-device-width: 414px) {
            u~div .email-container {
                min-width: 414px !important;
            }
        }

        a {
            color: #f3a333;
        }

        @media screen and (max-width: 500px) {

            .icon {
                text-align: left;
            }

            .text-services {
                padding-left: 0;
                padding-right: 20px;
                text-align: left;
            }

        }
    </style>
    <meta name="robots" content="noindex, follow">
</head>

<body width="100%" style="margin: 0; padding: 0 !important;">
	<#setting locale="en_US">
    <center style="width: 100%; background-color: white;">
        <div
            style="display: none; font-size: 1px;max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden; font-family: sans-serif;">
            &zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;
        </div>
        <div style="max-width: 600px; margin: 0 auto;" class="email-container">
            <table align="center" border="0" cellpadding="0" cellspacing="0" width="600"
                style="border-collapse: collapse; border: 1px solid black;">
                <tr>
                    <td align="center" style="padding: 20px 0 30px 0;">
                        <a href="https://seller-dev.DocuIt.com/" target="_blank"><img
                                src="" alt="DocuIt"
                                height="25px" style="display: block;color:white" /></a>                        
                    </td>
                </tr>
                <tr>
                    <td style="padding: 0px 30px 0px 30px;">
                        <p><b>Hello,</b></p>
                        <p>You are been invited to DocuIt app by <b>${invitedBy}</b>. Please register to Docuit and join <b>${invitedBy}</b>'s family</p>
                        <p><b>Cheers,</b><br><b>The DocuIt Team</b></p>
                    </td>
                </tr>
                <tr>
                   <#-- <td align="center">
                        <div style="margin: 0 auto; width: 200px">
                            <a href="https://www.facebook.com/DocuIt/" target="_blank">
                                <img src="https://seller-dev.DocuIt.com/assets/mail/fb.png" alt="DocuIt" height="40px"
                                    style="display: block;color:white;padding:5px;float: left;" /></a>
                            <a href="https://twitter.com/DocuIt" target="_blank"><img
                                    src="https://seller-dev.DocuIt.com/assets/mail/twitter.png" alt="DocuIt"
                                    height="40px" style="display: block;color:white;padding:5px;float: left;" /></a>
                            <a href="https://www.pinterest.com/DocuIt/_saved/" target="_blank"><img
                                    src="https://seller-dev.DocuIt.com/assets/mail/printset.png" alt="DocuIt"
                                    height="40px" style="display: block;color:white;padding:5px;float: left;" /></a>
                            <a href="https://www.instagram.com/DocuIt/" target="_blank"><img
                                    src="https://seller-dev.DocuIt.com/assets/mail/insta.png" alt="DocuIt" height="40px"
                                    style="display: block;color:white;padding:5px;float: left;" /></a>
                        </div>
                    </td> -->
                </tr>
                <tr>
                    <td align="center" style="padding: 20px 0 30px 0;">
                        <span>
                            <p class="footer-link footer-desc">Send with &#x2661; from DocuIt</p>
                            <p class="footer-link footer-desc"><a style="color:#ccc;" href="#">Unsubscribe</a></p> 
                            <p class="footer-link footer-desc">DocuIt</p>
                            <!-- <p class="footer-link">12140 Wallace Woods Lane</p>
                            <p class="footer-link">Alpharetta, Georgia 30004</p>
                            <p class="footer-link">United States</p>
                            <p class="footer-link">(404) 392-6970</p> -->
                        </span>
                    </td>
                </tr>
            </table>
        </div>
    </center> 
</body>

</html>