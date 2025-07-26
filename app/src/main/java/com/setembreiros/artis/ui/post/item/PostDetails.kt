package com.setembreiros.artis.ui.post.item

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.setembreiros.artis.R
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostContent
import com.setembreiros.artis.domain.model.post.PostMetadata
import com.setembreiros.artis.ui.commponents.AVPost
import com.setembreiros.artis.ui.commponents.DeleteAlertDialog
import com.setembreiros.artis.ui.commponents.ImagePost
import com.setembreiros.artis.ui.commponents.MenuOption
import com.setembreiros.artis.ui.commponents.TextPost
import com.setembreiros.artis.ui.commponents.ThreeDotsMenuButton
import com.setembreiros.artis.ui.theme.ArtisTheme
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.setembreiros.artis.domain.model.Comment
import com.setembreiros.artis.domain.model.Like
import com.setembreiros.artis.domain.model.Review
import com.setembreiros.artis.domain.model.Superlike
import com.setembreiros.artis.ui.commponents.DynamicColumn
import com.setembreiros.artis.ui.commponents.ShowErrorToast
import com.setembreiros.artis.ui.commponents.button.like.LikeButton
import com.setembreiros.artis.ui.commponents.button.like.LikeItem
import com.setembreiros.artis.ui.commponents.button.like.SuperlikeButton
import com.setembreiros.artis.ui.commponents.button.like.SuperlikeItem
import com.setembreiros.artis.ui.commponents.comment.CommentAction
import com.setembreiros.artis.ui.commponents.comment.CommentItem
import com.setembreiros.artis.ui.commponents.review.ReviewAction
import com.setembreiros.artis.ui.commponents.review.ReviewCard
import com.setembreiros.artis.ui.commponents.review.ReviewItem

@Composable
fun PostDetailsView(context: Context, post: Post, onChange: () -> Unit) {
    val viewModel: PostDetailsViewModel = hiltViewModel()
    LaunchedEffect(post.metadata.postId) {
        viewModel.initializeReviews(post.metadata.postId)
        viewModel.initializeComments(post.metadata.postId)
        viewModel.initializeLikes(post.metadata.postId)
        viewModel.initializeSuperlikes(post.metadata.postId)
    }
    val amountOfReviewsByPost by viewModel.amountOfReviewsByPost.collectAsState()
    val reviewCount by remember {
        derivedStateOf {
            amountOfReviewsByPost[post.metadata.postId] ?: post.metadata.reviews
        }
    }
    val amountOfCommentsByPost by viewModel.amountOfCommentsByPost.collectAsState()
    val commentCount by remember {
        derivedStateOf {
            amountOfCommentsByPost[post.metadata.postId] ?: post.metadata.comments
        }
    }
    val likesByPost by viewModel.amountOfLikesByPost.collectAsState()
    val likedByUser by viewModel.likedByUser.collectAsState()
    val likesCount = likesByPost[post.metadata.postId] ?: post.metadata.likes
    val isLiked = likedByUser[post.metadata.postId] ?: post.metadata.isLikedByCurrentUser
    val superlikesByPost by viewModel.amountOfSuperlikesByPost.collectAsState()
    val superlikedByUser by viewModel.superlikedByUser.collectAsState()
    val superlikesCount = superlikesByPost[post.metadata.postId] ?: post.metadata.superlikes
    val isSuperliked = superlikedByUser[post.metadata.postId] ?: post.metadata.isSuperlikedByCurrentUser
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val postReviews by viewModel.postReviews.collectAsState()
    val thereAreMoreReviews by viewModel.thereAreMoreReviews.collectAsStateWithLifecycle()
    var showReviews by remember { mutableStateOf(false) }
    val postComments by viewModel.postComments.collectAsState()
    val thereAreMoreComments by viewModel.thereAreMoreComments.collectAsStateWithLifecycle()
    var showComments by remember { mutableStateOf(false) }
    val postLikes by viewModel.postLikes.collectAsState()
    val thereAreMoreLikes by viewModel.thereAreMoreLikes.collectAsStateWithLifecycle()
    var showLikes by remember { mutableStateOf(false) }
    val postSuperlikes by viewModel.postSuperlikes.collectAsState()
    val thereAreMoreSuperlikes by viewModel.thereAreMoreSuperlikes.collectAsStateWithLifecycle()
    var showSuperlikes by remember { mutableStateOf(false) }
    val errorCode by viewModel.errorCode.collectAsState()

    // Mostrar Toast cando haxa un erro
    ShowErrorToast(
        errorCode = errorCode,
        context = context,
        clearError = { viewModel.clearErrorMessage() }
    )

    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteAlertDialog(
            onConfirm = {
                viewModel.deletePost(
                    post.metadata.postId,
                    onSuccess = {
                        onChange()
                        showDeleteDialog = false
                    }
                )
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    PostHeader(
        username = post.metadata.username,
        onDeleteClick = { showDeleteDialog = true }
    )
    PostTitle(title = post.metadata.title)
    RenderPostContent(post = post, context = context)
    PostActionsRow(
        reviewCount = reviewCount,
        commentCount = commentCount,
        isLiked = isLiked,
        likesCount = likesCount,
        isSuperliked = isSuperliked,
        superlikesCount = superlikesCount,
        onReviewClick = {
            viewModel.loadInitialReviews(post.metadata.postId)
            showReviews = true
        },
        onCommentClick = {
            viewModel.loadInitialComments(post.metadata.postId)
            showComments = true
        },
        onLike = { viewModel.toggleLikePost(post.metadata.postId) },
        onShowLikes = {
            viewModel.loadInitialLikes(post.metadata.postId)
            showLikes = true
        },
        onSuperlike = { viewModel.toggleSuperlikePost(post.metadata.postId) },
        onShowSuperlikes = {
            viewModel.loadInitialSuperlikes(post.metadata.postId)
            showSuperlikes = true
        }
    )
    PostDescription(description = post.metadata.description)

    if (showReviews) {
        ReviewsSection(
            postReviews,
            onLoadMore = {
                viewModel.loadMoreReviews(post.metadata.postId)
            },
            isLoading,
            thereAreMoreReviews,
            onSend = {
                viewModel.addReviewAndUpdate(post.metadata.postId, it.title, it.content, it.rating)
            },
            onDismiss = { showReviews = false },
            onReviewAction = { action ->
                when (action) {
                    is ReviewAction.Delete -> {
                        viewModel.deleteReviewAndUpdate(post.metadata.postId, action.review.reviewId)
                    }
                }
            }
        )
    }

    if (showComments) {
        CommentsSection(
            postComments,
            onLoadMore = {
                viewModel.loadMoreComments(post.metadata.postId)
            },
            isLoading,
            thereAreMoreComments,
            onSend = {
                viewModel.addCommentAndUpdate(post.metadata.postId, it)
            },
            onDismiss = { showComments = false },
            onCommentAction = { action ->
                when (action) {
                    is CommentAction.Delete -> {
                        viewModel.deleteCommentAndUpdate(post.metadata.postId, action.comment.commentId)
                    }
                }
            }
        )
    }

    if (showLikes) {
        LikeSection(
            postLikes,
            onLoadMore = {
                viewModel.loadMoreLikes(post.metadata.postId)
            },
            isLoading,
            thereAreMoreLikes,
            onDismiss = { showLikes = false }
        )
    }

    if (showSuperlikes) {
        SuperlikeSection(
            postSuperlikes,
            onLoadMore = {
                viewModel.loadMoreSuperlikes(post.metadata.postId)
            },
            isLoading,
            thereAreMoreSuperlikes,
            onDismiss = { showSuperlikes = false }
        )
    }
}

@Composable
fun PostHeader(
    username: String,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://img.freepik.com/premium-vector/business-office-african-american-manager-usinessman-avatar-icon-head-portrait-occupation_805465-135.jpg",
                placeholder = painterResource(id = R.drawable.male_avatar_placeholder),
                contentDescription = stringResource(R.string.avatar_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = username,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        val profileOptions = listOf(
            MenuOption(
                text = "Delete",
                color = Color.Red,
                icon = Icons.Default.Delete,
                onClick = onDeleteClick
            )
        )

        ThreeDotsMenuButton(profileOptions)
    }
}

@Composable
fun PostTitle(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun RenderPostContent(
    post: Post,
    context: Context
) {
    // Convertir contido en URI temporal se é necesario
    if(post.content?.content != null && post.content?.content!!.isNotEmpty()) {
        post.content!!.uriContent = createUriTempFile(
            context = context,
            postId = post.metadata.postId,
            content = post.content?.content
        )
        post.content?.content = null
    }

    // Mostrar contido segudo o tipo
    when (post.metadata.type) {
        Constants.ContentType.TEXT -> TextPost(post.content!!.uriContent)
        Constants.ContentType.IMAGE -> ImagePost(post.content!!.uriContent)
        Constants.ContentType.AUDIO,
        Constants.ContentType.VIDEO -> AVPost(post.content!!.uriContent)
    }
}

@Composable
fun PostActionsRow(
    reviewCount: Long,
    commentCount: Long,
    isLiked: Boolean,
    likesCount: Long,
    isSuperliked: Boolean,
    superlikesCount: Long,
    onReviewClick: () -> Unit,
    onCommentClick: () -> Unit,
    onLike: () -> Unit,
    onShowLikes: () -> Unit,
    onSuperlike: () -> Unit,
    onShowSuperlikes: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReviewsButton(
            reviewCount = reviewCount,
            onClick = onReviewClick
        )
        CommentsButton(
            commentCount = commentCount,
            onClick = onCommentClick
        )
        LikeButton(
            isLiked = isLiked,
            likesCount = likesCount,
            onLike = onLike,
            onShow = onShowLikes
        )
        SuperlikeButton(
            isSuperliked = isSuperliked,
            superlikesCount = superlikesCount,
            onSuperlike = onSuperlike,
            onShow = onShowSuperlikes
        )
    }
}

@Composable
fun PostDescription(description: String) {
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = description,
        fontSize = 18.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ReviewsButton(
    reviewCount: Long,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .padding(end = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.RateReview,
            contentDescription = "Reviews",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$reviewCount",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReviewsSection(
    reviews: List<Review>,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    thereAreMoreReviews: Boolean,
    onSend: (ReviewContent) -> Unit,
    onDismiss: () -> Unit,
    onReviewAction: (ReviewAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var newReview by remember { mutableStateOf(ReviewContent("", "", 0)) }

    ModalBottomSheet(
        onDismissRequest = {
            keyboardController?.hide()
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        windowInsets = WindowInsets(0, 0, 0, 0),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .imeNestedScroll() // Importante para o comportamento correcto
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp) // Espazo para o campo fixo
            ) {
                // Cabeceira
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.review_section),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    AddReviewButton(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        onClick = { }
                    )
                }

                HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                // Lista de reviews
                DynamicColumn(
                    items = reviews,
                    itemView = { review ->
                        ReviewCard(
                            review = review,
                            onAction = onReviewAction
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                    },
                    onLoadMore = onLoadMore,
                    isLoading = isLoading,
                    thereAreMoreItems = thereAreMoreReviews,
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(bottom = 56.dp),
                )
            }
        }
    }
}

@Composable
fun AddReviewButton(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add review",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(id = R.string.add_review))
        }
    }
}

@Composable
fun CommentsButton(
    commentCount: Long,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .padding(end = 16.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Comment,
            contentDescription = "Comments",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$commentCount",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CommentsSection(
    comments: List<Comment>,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    thereAreMoreComments: Boolean,
    onSend: (String) -> Unit,
    onDismiss: () -> Unit,
    onCommentAction: (CommentAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var newComment by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = {
            keyboardController?.hide()
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        windowInsets = WindowInsets(0, 0, 0, 0),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .imeNestedScroll() // Importante para o comportamento correcto
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp) // Espazo para o campo fixo
            ) {
                // Cabeceira
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.comment_section),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                // Lista de comentarios
                DynamicColumn(
                    items = comments,
                    itemView = { comment ->
                        CommentItem(
                            comment = comment,
                            onAction = onCommentAction
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                    },
                    onLoadMore = onLoadMore,
                    isLoading = isLoading,
                    thereAreMoreItems = thereAreMoreComments,
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(bottom = 56.dp),
                )
            }

            // Campo de comentario
            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp)
                    .navigationBarsPadding()
                    .imePadding() // Só este elemento reacciona ao teclado
            ) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(stringResource(id = R.string.add_comment), color = Color.LightGray) }
                        )
                        IconButton(
                            onClick = {
                                if (newComment.isNotBlank()) {
                                    onSend(newComment)
                                    newComment = ""
                                    keyboardController?.hide()
                                }
                            },
                            enabled = newComment.isNotBlank()
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (newComment.isNotBlank()) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LikeSection(
    likes: List<Like>,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    thereAreMoreLikes: Boolean,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        windowInsets = WindowInsets(0, 0, 0, 0),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .imeNestedScroll() // Importante para o comportamento correcto
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp) // Espazo para o campo fixo
            ) {
                // Cabeceira
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.like_section),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                DynamicColumn(
                    items = likes,
                    itemView = { like ->
                        LikeItem(
                            like = like,
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                    },
                    onLoadMore = onLoadMore,
                    isLoading = isLoading,
                    thereAreMoreItems = thereAreMoreLikes,
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(bottom = 56.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SuperlikeSection(
    superlikes: List<Superlike>,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    thereAreMoreSuperlikes: Boolean,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        windowInsets = WindowInsets(0, 0, 0, 0),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .imeNestedScroll() // Importante para o comportamento correcto
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp) // Espazo para o campo fixo
            ) {
                // Cabeceira
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.superlike_section),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                // Lista de comentarios
                DynamicColumn(
                    items = superlikes,
                    itemView = { superlike ->
                        SuperlikeItem(
                            superlike = superlike,
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                    },
                    onLoadMore = onLoadMore,
                    isLoading = isLoading,
                    thereAreMoreItems = thereAreMoreSuperlikes,
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(bottom = 56.dp),
                )
            }
        }
    }
}

@Composable
private fun createUriTempFile(context: Context, postId: String, content: ByteArray?): Uri? {
    content?.let {
        val tempFile = createTempFile(context, postId, content)

        tempFile?.let {
            return remember {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    tempFile
                )
            }
        }

        return null
    }

    return null
}

@Composable
private fun createTempFile(context: Context, postId: String, content: ByteArray?): File? {
    content?.let {
        val tempFile = remember {
            val file = File.createTempFile("temp_$postId", "", context.cacheDir)
            val fos = FileOutputStream(file)
            fos.write(content)
            fos.close()
            file
        }

        return tempFile
    }

    return null
}

data class ReviewContent(val title: String, val content: String, val rating: Int)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImagePostDetailsPreview() {
    val context = LocalContext.current
    val imageResource = context.resources.openRawResource(R.raw.imaxe_de_proba)
    val content = imageResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.IMAGE,
            title = "Sample Title",
            description = "This is a sample description for the post.", 5, 0, 0,"", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
    )

    ArtisTheme {
        PostDetailsView(context, post = samplePost, {})
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Image2PostDetailsPreview() {
    val context = LocalContext.current
    val imageResource = context.resources.openRawResource(R.raw.image_test_2)
    val content = imageResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.IMAGE,
            title = "Sample Title",
            description = "This is a sample description for the post.", 0, 0,0,"", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
    )

    ArtisTheme {
        PostDetailsView(context, post = samplePost, {})
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Video1PostDetailsPreview() {
    val context = LocalContext.current
    val videoResource = context.resources.openRawResource(R.raw.video_test_1)
    val content = videoResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.VIDEO,
            title = "Sample Title",
            description = "This is a sample description for the post.", 0, 0, 0,"", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
    )

    ArtisTheme {
        PostDetailsView(context, samplePost, {})
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Video2PostDetailsPreview() {
    val context = LocalContext.current
    val videoResource = context.resources.openRawResource(R.raw.video_test_2)
    val content = videoResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.VIDEO,
            title = "Sample Title",
            description = "This is a sample description for the post.", 0, 0, 0,"", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
    )

    ArtisTheme {
        PostDetailsView(context, samplePost, {})
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PdfPostDetailsPreview() {
    val context = LocalContext.current
    val pdfResourceDescriptor = context.resources.openRawResourceFd(R.raw.pdf_test)
    val inputStream: InputStream = pdfResourceDescriptor.createInputStream()
    val buffer = ByteArrayOutputStream()
    val data = ByteArray(1024)  // Buffer to read data in chunks
    var nRead: Int

    while (inputStream.read(data, 0, data.size).also { nRead = it } != -1) {
        buffer.write(data, 0, nRead)
    }

    buffer.flush()

    val content = buffer.toByteArray()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.TEXT,
            title = "Sample Title",
            description = "This is a sample description for the post.", 0, 0, 0,"", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
    )

    ArtisTheme {
        PostDetailsView(context, samplePost, {})
    }
}