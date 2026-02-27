package com.softnerve.epic.constant;

public final class EpicConstants {

    private EpicConstants() {}

    public static final String CLIENT_ID =
            "62dee9a7-1409-4019-89f6-59dd307e37d8";

    public static final String TOKEN_URL =
            "https://fhir.epic.com/interconnect-fhir-oauth/oauth2/token";

    public static final String FHIR_BASE =
            "https://fhir.epic.com/interconnect-fhir-oauth/api/FHIR/R4";
    public static final String FHIR_BASE1 =
            "https://fhir.epic.com/interconnect-fhir-oauth/api/FHIR/STU3";

    public static final String KID = "va-backend-sys1";

    public static final String PRIVATE_JWK_JSON = """
    {
      "p":"-5Pm0vIpuEr8QIAOQRuOxWV86QFl2UbwfSKP2rkt_7IS-2SFdRXllTvrBlGMTYAmPvGrUR1Vl4grdi_YxDgUBr0nRzgjhDa4OT5HVqaiPqZ7o-c_XF-QxPFOjEWmOuLKN3vevMcFO_BwdBq_XVrOx0lLLytybUEOrqwoO5gLkOs",
      "kty":"RSA",
      "q":"6YAh2f6hhWJhrGnlU3L9hkH2Hi1Hd9Pu7X49u03pcMHqB0s76_0O8kCqyruHE1seBAo0UdLbDo9qpg4ekajAylpwjceBkvt-L9d-WLkk7xYKu0873IAkPntc_y4on7u4Gl1xQ3puNack6G2aRhl8Ph0PzTXDoeEhfdRyQO0Y-6M",
      "d":"BGDwZlaY84Ncw4CdcPESqlw9DYiQXoNRyMUVDifJTggQcloblHiZptmJ30iwlT6a__20MdqdZr7gU6lWPfeBYOuqPOuZw_7H9n54e_pAzEDS-beXpWFCnks5op9_hGhSG20h4lG4yrR5csNX79tmjJps4LK2jvf4lRTqGb5kSuMnSU5ZZJIvA-pUrn1IsvUU9iYnPTaXZ1y_0ZxPbVipT6yXFqbVi2UwnUpXz8kEo-vTdtjUoJHeWRHqgEefH0meGOBlYHnCyO5qwV9Sy90E98bXhATdQHo2fsgTWyUMIC66oCGq5DHe26PrkLQ52vP1Axh81WAIQThmNyvkkjrm2Q",
      "e":"AQAB",
      "use":"sig",
      "kid":"va-backend-sys1",
      "qi":"tiMMQ4kxgMYp2VgxSqUa1XgRCCnUJrjWBCDMF5QW0M3jD3UDNhE6gY_pf6SL8QJgY7J3kiUqTBxfO67iVeQZgAph0OtcCVOMoe8ODWsqrk-IG6bwyo2o8HqP5QJ592AKBpCrNGlVjk0C8BdFAhXt2LDYoA7mjzGznID74Gm-XK4",
      "dp":"yFHEgkOBJNXQdiuBLiabUGK3S3Fpf9EUvQ9ALJE3J1rL8pDgE5MT8_h589YLOcrIqWYVW8lX1gj_UmRW3icpCxSiqfrxjN1lY29LrpuQpQI7gQ8pTIxr4eWi_d6XtlvPWM6xVO_EIy4Y3NpPdi5QaC7cRvr5ajVbU4qRnqI603E",
      "alg":"RS384",
      "dq":"KcvQw2er2-dfWnBeJ1DXBrg56O7r8wwRoZtOS3_HwHJFhOa3pCdZDI38xGtK43EU714wPVFD-tg7G9w9Jo-RaudDbG2AySGRVdj5-cGyVcWZSCwHqBGb6z9Dxz2o8ea1Lqr5Qj3hCmOP3Lb5vYCEV8317SKpkQluaEql5O9L4Ic",
      "n":"5XeITbM8Yb0HqdZu3DGiTcxLOg7k9xrKJrh-D2EJjf7eoV0ZJrJ4fR1VRIukGkUurZ4_MWmI2M_KtgC9l1BnQlzwnuCskpCSmMy7MYOyfZzDonpIuT14Vab3qeeFveA6yWR1GMbK7BS8aGMLtZXPsKgLopHzIEbLhKnqFb29s_omy5w1DdWCL7gAYfX81L8pv9U4aoiYkTlvykwSthq7cb7gm8uYoC1wineg8Svpyu1XH9hks6PSvBt9uO6diUBdzCDNodRuXggkJMLajAPxIX5yv5hvA_oNZT33nJkSUTAieYLrSfh8BQ5TmEZG0T_1tOszn1Oi9DvfceMES3uuoQ"
    }
    """;
}
