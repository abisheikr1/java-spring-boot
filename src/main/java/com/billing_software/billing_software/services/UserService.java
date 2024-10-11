package com.billing_software.billing_software.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.dbInterface.UserInterface;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.models.organization.Outlet;
import com.billing_software.billing_software.models.user.User;
import com.billing_software.billing_software.payloads.requests.UserUpsertRequest;
import com.billing_software.billing_software.utils.cloudinary.CloudinaryService;
import com.billing_software.billing_software.utils.commons.EmailService;
import com.billing_software.billing_software.utils.crypto.CryptoService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    private final UserInterface userInterface;
    private final OrganizationInterface organizationInterface;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;

    // Constructor injection for required dependencies
    public UserService(UserInterface userInterface, OrganizationInterface organizationInterface,
            CloudinaryService cloudinaryService, EmailService emailService) {
        this.userInterface = userInterface;
        this.organizationInterface = organizationInterface;
        this.cloudinaryService = cloudinaryService;
        this.emailService = emailService;
    }

    // Creates or updates a user based on the request's ID
    public String createOrUpdateUser(UserUpsertRequest userUpsertRequest) {
        User userData = new User(userUpsertRequest);

        if (userUpsertRequest.orgId != null && organizationInterface.get(userUpsertRequest.orgId) == null) {
            throw new RuntimeException("Invalid org Id");
        }

        if (userUpsertRequest.id != null) {
            if (userInterface.get(userUpsertRequest.id) != null) {
                return userInterface.update(userData);
            } else {
                throw new RuntimeException("Invalid user Id");
            }
        } else {
            return userInterface.create(userData);
        }
    }

    // Retrieves user data by user ID and hides secret for security
    public User getUserById(String userId) {
        User userData = userInterface.get(userId);
        userData.setSecret(null);
        return userData;
    }

    // Fetches all users matching the search text
    public List<User> getAllUserWithSearch(String searchText) {
        return userInterface.getAllWithSearch(searchText);
    }

    // Uploads a profile picture for the user
    public Map<String, Object> uploadProfile(MultipartFile file, String userId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        User userDetails = userInterface.get(userId);
        if (userDetails == null) {
            throw new RuntimeException("Invalid user Id");
        }

        String imageUrl = cloudinaryService.uploadImage(file, "organization/" + orgId + "/user/" + userId + "/profile",
                "profile");
        userDetails.setProfile(imageUrl, "profile");
        userInterface.update(userDetails);

        return userDetails.getProfile();
    }

    // Deletes the user's profile picture
    public String deleteProfile(String userId, String publicUrl) {
        User userDetail = userInterface.get(userId);
        if (userDetail == null) {
            throw new RuntimeException("Invalid user Id");
        }

        userDetail.getProfile().remove("profile");
        cloudinaryService.deleteImage(publicUrl);
        userInterface.update(userDetail);

        return "Deleted successfully";
    }

    // Sends a password reset link to the user's email
    public void sendResetPasswordLink(String email) {
        User user = userInterface.getByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Organization orgDetails = organizationInterface.get(user.getOrgId());
        Outlet outletFound = orgDetails.getOutlets().get(0); // Get outlet details

        String resetToken = generateResetToken(); // Generate reset token
        String resetLink = "https://billing-software-fe.onrender.com/org/login/" + orgDetails.getId()
                + "/reset_password?token="
                + resetToken + "&userId=" + user.getId(); // Create

        String htmlBodyTemplate = "<html>" +
                "<body>" +
                "<p>Please click the button below to reset your password:</p>" +
                "<a href=\"" + resetLink
                + "\" style=\"display: inline-block; padding: 10px 20px; margin: 10px 0; font-size: 16px; color: #ffffff; background-color: #007bff; text-decoration: none; border-radius: 5px;\">"
                +
                "Reset Password" +
                "</a>" +
                "<p>If you did not request a password reset, please ignore this email.</p>" +
                "</body>" +
                "</html>";
        // Send email using SMTP configuration
        emailService.sendEmail(
                outletFound.getSMTPConfig().get("username"),
                outletFound.getSMTPConfig().get("password"),
                user.getEmail(),
                "Password Reset Request Form Invo",
                "Dear " + user.getName() + ",\n\n" +
                        htmlBodyTemplate,
                null);

        user.setResetToken(resetToken); // Store reset token in user's record
        userInterface.update(user);
    }

    // Generates a secure reset token (UUID)
    private String generateResetToken() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Verifies the reset token and updates the user's password.
     * 
     * @param token:       The reset token to validate.
     * @param newPassword: The new password to be set for the user.
     */
    public void updatePassword(String userId, String token, String newPassword) {
        // Find the user associated with the reset token
        User user = userInterface.get(userId);
        if (user == null) {
            throw new RuntimeException("Invalid user id expired");
        }

        if (!user.getResetToken().equals(token)) {
            throw new RuntimeException("Invalid token to reset");
        }

        // Validate token expiration if applicable (optional)
        // Add logic here if tokens have an expiration time

        // Update the user's password
        try {
            user.setPassword(CryptoService.encrypt(newPassword, user.getSecret()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Clear the reset token after successful password update
        user.setResetToken(null);
        userInterface.update(user);
    }

}